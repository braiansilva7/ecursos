package com.ecursos.myapp.service;

import com.ecursos.myapp.domain.Militar;
import com.ecursos.myapp.repository.MilitarRepository;
import com.ecursos.myapp.repository.search.MilitarSearchRepository;
import com.ecursos.myapp.service.dto.MilitarDTO;
import com.ecursos.myapp.domain.MilitarApi;
import com.ecursos.myapp.domain.FotoResponse;
import com.ecursos.myapp.service.mapper.MilitarMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.HttpHeaders;
import java.util.Map;
import java.util.HashMap;
import org.springframework.core.ParameterizedTypeReference;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import java.util.stream.Collectors;
import java.util.Collections;
import org.springframework.dao.DataIntegrityViolationException;
import com.ecursos.myapp.web.rest.errors.BadRequestAlertException;
import com.ecursos.myapp.service.errors.MilitarServiceException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.io.IOException;
import java.io.FileNotFoundException;
import org.springframework.web.client.RestClientException;
import jakarta.servlet.http.HttpServletRequest;
import java.nio.file.*;
import java.util.Arrays;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import jakarta.annotation.PostConstruct;
import java.util.*;
import com.ecursos.myapp.service.MinIOService;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import io.minio.errors.ErrorResponseException;

/**
 * Service Implementation for managing {@link com.ecursos.myapp.domain.Militar}.
 */
@Service
@Transactional
public class MilitarService {

    private static final Logger LOG = LoggerFactory.getLogger(MilitarService.class);

    private final MilitarRepository militarRepository;

    private final MilitarMapper militarMapper;

    private final MilitarSearchRepository militarSearchRepository;

    private final WebClient webClient;

    private final MinIOService minIOService;

    @Value("${ecursos.webservice.militar-saram}")
    private String militarSaram;

    @Value("${ecursos.webservice.login-url}")
    private String loginUrl;

    @Value("${ecursos.webservice.api-fotos}")
    private String apiFotos;

    @Value("${ecursos.webservice.militar-url}")
    private String militarUrl;

    @Value("${ecursos.webservice.militar-url-cdcaer}")
    private String militarUrlCdcaer;

    @Value("${ecursos.webservice.username}")
    private String username;

    @Value("${ecursos.webservice.password}")
    private String password;

    @Value("${ecursos.webservice.photo-directory}")
    private String photoDirectory;

    @Value("${ecursos.webservice.photo-directory-local}")
    private String photoDirectoryLocal;

    private static final String CCABR_PREFIX = "efetivo/";

    public MilitarService(
        MilitarRepository militarRepository,
        MilitarMapper militarMapper,
        MilitarSearchRepository militarSearchRepository,
        WebClient.Builder webClientBuilder, 
        MinIOService minIOService
    ) {
        this.militarRepository = militarRepository;
        this.militarMapper = militarMapper;
        this.militarSearchRepository = militarSearchRepository;
        this.webClient = webClientBuilder.build();
        this.minIOService = minIOService;
    }

    @PostConstruct
    public void reindexAllMilitares() {
        militarSearchRepository.deleteAll();
        List<Militar> allMilitares = militarRepository.findAll();
        militarSearchRepository.saveAll(allMilitares);
        
        LOG.debug("Reindexação completa de todos os MILITARES no Elasticsearch");
    }

    public Optional<MilitarApi> getConsultaSaram(String saram) {
        // Autentica e obtém o token
        String token = authenticateAndGetToken();

        // Configura os headers da requisição
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        // Cria a entidade HTTP (com os headers)
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Cria o RestTemplate
        RestTemplate restTemplate = new RestTemplate();

        // Faz a requisição GET para obter o militar pelo SARAM
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                militarSaram + saram,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<Map<String, Object>>() {}
        );

        // Verifica se a resposta é nula e retorna um Optional vazio, caso necessário
        if (response.getBody() == null || response.getBody().isEmpty()) {
            return Optional.empty();
        }

        // Mapeia os dados necessários para a classe MilitarApi
        Map<String, Object> data = response.getBody();
        MilitarApi militarApi = new MilitarApi();
        militarApi.setSgOrg(data.get("organizacao") != null ? data.get("organizacao").toString() : null);
        militarApi.setPosto(data.get("codigo_posto") != null ? data.get("codigo_posto").toString() : null);
        militarApi.setGuerra(data.get("nome_guerra") != null ? data.get("nome_guerra").toString() : null);
        militarApi.setPessoa(data.get("nome_completo") != null ? data.get("nome_completo").toString() : null);
        militarApi.setCpf(data.get("cpf") != null ? data.get("cpf").toString() : null);
        militarApi.setEmail(data.get("email") != null ? data.get("email").toString() : null);
        militarApi.setNrAntiguidade(data.get("nr_antiguidade") != null ? data.get("nr_antiguidade").toString() : null);
        militarApi.setUltimaPromocao(data.get("ultima_promocao") != null && !data.get("ultima_promocao").toString().isEmpty() 
            ? LocalDate.parse(data.get("ultima_promocao").toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            : null);
        militarApi.setOrdem(data.get("saram") != null ? data.get("saram").toString() : null);
        militarApi.setPerfil(data.get("pesfis_type") != null ? data.get("pesfis_type").toString() : null);
        militarApi.setTelefoneCel(data.get("telefone_cel") != null ? data.get("telefone_cel").toString() : null);

        return Optional.of(militarApi);
    }


    // Método para chamar a API e obter os dados dos militares de ambas as URLs
    public List<MilitarApi> getAllMilitarsOM() {
        // Autentica e obtém o token
        String token = authenticateAndGetToken();

        // Configura os headers da requisição
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        // Cria a entidade HTTP (com os headers)
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Cria o RestTemplate
        RestTemplate restTemplate = new RestTemplate();

        // Obtém os militares da primeira URL
        List<Map<String, Object>> militares1 = fetchMilitaresFromUrl(restTemplate, militarUrl, entity);

        // Obtém os militares da segunda URL
        List<Map<String, Object>> militares2 = fetchMilitaresFromUrl(restTemplate, militarUrlCdcaer, entity);

        // Combina os resultados
        List<Map<String, Object>> allMilitares = new ArrayList<>();
        allMilitares.addAll(militares1);
        allMilitares.addAll(militares2);

        // Combina os resultados e remove duplicatas, mantendo a última ocorrência
        Map<String, MilitarApi> militarMap = new LinkedHashMap<>();

        for (Map<String, Object> militarData : militares1) {
            MilitarApi militar = mapToMilitarApi(militarData);
            militarMap.put(militar.getOrdem(), militar); // Insere no mapa, sobrescrevendo caso já exista
        }

        for (Map<String, Object> militarData : militares2) {
            MilitarApi militar = mapToMilitarApi(militarData);
            militarMap.put(militar.getOrdem(), militar); // Sobrescreve se já existir um SARAM duplicado
        }

        // Retorna a lista final de militares sem duplicações
        return new ArrayList<>(militarMap.values());
    }

    // Método genérico para consumir os militares de uma URL específica
    private List<Map<String, Object>> fetchMilitaresFromUrl(RestTemplate restTemplate, String url, HttpEntity<String> entity) {
        ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {}
        );
        return response.getBody() != null ? response.getBody() : Collections.emptyList();
    }

    // Método para mapear um militar da API para MilitarApi
    private MilitarApi mapToMilitarApi(Map<String, Object> data) {
        MilitarApi militarApi = new MilitarApi();
        militarApi.setOrg(data.get("cd_org") != null ? data.get("cd_org").toString() : "");
        militarApi.setPosto(data.get("cd_posto") != null ? data.get("cd_posto").toString() : "");
        militarApi.setGuerra(data.get("nm_guerra") != null ? data.get("nm_guerra").toString() : "");
        militarApi.setPessoa(data.get("nm_pessoa") != null ? data.get("nm_pessoa").toString() : "");
        militarApi.setCpf(data.get("nr_cpf") != null ? data.get("nr_cpf").toString() : "");

        // 🔥 Captura o nm_email da lista emails
        if (data.get("emails") instanceof List<?> emailsList && !emailsList.isEmpty()) {
            Object firstEmail = emailsList.get(0);
            if (firstEmail instanceof Map<?, ?> emailMap && emailMap.get("nm_email") != null) {
                militarApi.setEmail(emailMap.get("nm_email").toString());
            } else {
                militarApi.setEmail("");
            }
        } else {
            militarApi.setEmail("");
        }

        militarApi.setNrAntiguidade(data.get("nr_antiguidade") != null ? data.get("nr_antiguidade").toString() : "");
        militarApi.setUltimaPromocao(data.get("dt_promocao_atual") != null && !data.get("dt_promocao_atual").toString().isEmpty()
                ? LocalDate.parse(data.get("dt_promocao_atual").toString().substring(0, 10), DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                : null);
        militarApi.setOrdem(data.get("nr_ordem") != null ? data.get("nr_ordem").toString() : "");
        militarApi.setPerfil(data.get("pesfis_type") != null ? data.get("pesfis_type").toString() : "");
        militarApi.setTelefoneCel(data.get("telefone_cel") != null ? data.get("telefone_cel").toString() : null);
        militarApi.setSgOrg(data.get("sg_org") != null ? data.get("sg_org").toString() : "");

        return militarApi;
    }

    @Transactional
    public void updateAll(List<MilitarDTO> militaresParaAtualizar) {
        List<Militar> militares = militaresParaAtualizar.stream()
            .map(militarMapper::toEntity)
            .collect(Collectors.toList());

        // Atualizar em lote no banco de dados
        militarRepository.saveAll(militares);

        for (Militar militarIndex : militares) {
            militarSearchRepository.save(militarIndex);
        }
    }

    public String downloadAndSavePhoto(String saram) {
        String fileName = saram + ".jpg";
        String fullPath = CCABR_PREFIX + fileName;

        try {
            RestTemplate restTemplate = new RestTemplate();
            String apiUrl = apiFotos + saram;
            FotoResponse response = restTemplate.getForObject(apiUrl, FotoResponse.class);

            if (response != null && response.getImFoto() != null) {
                byte[] decodedBytes = Base64.getDecoder().decode(response.getImFoto());
                InputStream inputStream = new ByteArrayInputStream(decodedBytes);

                return minIOService.uploadFile(
                    null,
                    fullPath,
                    inputStream,
                    "image/jpeg",
                    decodedBytes.length
                );
            } else {
                throw new FileNotFoundException("Foto não encontrada para o SARAM: " + saram);
            }

        } catch (ErrorResponseException e) {
            if (e.errorResponse().code().equals("NoSuchKey")) {
                return null;
            }
            throw new RuntimeException("Erro ao verificar imagem no MinIO", e);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar imagem no MinIO", e);
        }
    }

    public void saveAll(List<MilitarDTO> militaresDTO) {
        List<MilitarDTO> militaresFiltrados = militaresDTO.stream()
            .filter(militarDTO -> !militarRepository.existsBySaram(militarDTO.getSaram()))
            .collect(Collectors.toList());

        List<Militar> militaresParaInserir = militarMapper.toEntityList(militaresFiltrados);

        if (!militaresParaInserir.isEmpty()) {
            try {
                militarRepository.saveAll(militaresParaInserir);
                militarSearchRepository.saveAll(militaresParaInserir);
                
            } catch (DataIntegrityViolationException e) {
                LOG.error("Erro ao inserir militares: duplicação de SARAM detectada", e);
                throw new MilitarServiceException("Erro ao salvar militares: duplicação de SARAM detectada.", e);
            }
        }
    }

    private String authenticateAndGetToken() {
        // Cria o RestTemplate para a requisição de login
        RestTemplate restTemplate = new RestTemplate();

        // Cria o corpo da requisição com username e password
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("username", username);
        requestBody.put("password", password);

        // Cria headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        // Cria a entidade HTTP (headers + body)
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);

        // Faz a requisição POST para obter o token
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
            loginUrl,
            HttpMethod.POST,
            entity,
            new ParameterizedTypeReference<Map<String, Object>>() {}
        );

        LOG.debug("TOKEN OBTIDO: {}", (String) response.getBody().get("access_token"));
        // Extrai o token da resposta
        return (String) response.getBody().get("access_token");
    }

    /**
     * Save a militar.
     *
     * @param militarDTO the entity to save.
     * @return the persisted entity.
     */
    public MilitarDTO save(MilitarDTO militarDTO) {
        try {
            // Fazer o download e salvar a foto do militar
            downloadAndSavePhoto(militarDTO.getSaram());
        } catch (Exception e) {
            LOG.error("Falha ao baixar e salvar a foto para o SARAM {}: {}", militarDTO.getSaram(), e.getMessage());
        }
        LOG.debug("Request to save Militar : {}", militarDTO);
        Militar militar = militarMapper.toEntity(militarDTO);
        militar = militarRepository.save(militar);
        militarSearchRepository.save(militar);
        return militarMapper.toDto(militar);
    }

    /**
     * Update a militar.
     *
     * @param militarDTO the entity to save.
     * @return the persisted entity.
     */
    public MilitarDTO update(MilitarDTO militarDTO) {
        LOG.debug("Request to update Militar : {}", militarDTO);
        Militar militar = militarMapper.toEntity(militarDTO);
        militar = militarRepository.save(militar);
        militarSearchRepository.save(militar);
        return militarMapper.toDto(militar);
    }

    /**
     * Partially update a militar.
     *
     * @param militarDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<MilitarDTO> partialUpdate(MilitarDTO militarDTO) {
        LOG.debug("Request to partially update Militar : {}", militarDTO);

        return militarRepository
            .findById(militarDTO.getId())
            .map(existingMilitar -> {
                militarMapper.partialUpdate(existingMilitar, militarDTO);

                return existingMilitar;
            })
            .map(militarRepository::save)
            .map(savedMilitar -> {
                militarSearchRepository.save(savedMilitar);
                return savedMilitar;
            })
            .map(militarMapper::toDto);
    }

    /**
     * Get all the militars with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<MilitarDTO> findAllWithEagerRelationships(Pageable pageable) {
        return militarRepository.findAllWithEagerRelationships(pageable).map(militarMapper::toDto);
    }

    /**
     * Get one militar by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<MilitarDTO> findOne(Long id) {
        LOG.debug("Request to get Militar : {}", id);
        return militarRepository.findOneWithEagerRelationships(id).map(militarMapper::toDto);
    }

    /**
     * Delete the militar by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Militar : {}", id);
        militarRepository.deleteById(id);
        militarSearchRepository.deleteFromIndexById(id);
    }

    /**
     * Search for the militar corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<MilitarDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of Militars for query {}", query);
        return militarSearchRepository.search(query, pageable).map(militarMapper::toDto);
    }

    public Optional<MilitarDTO> findBySaram(String saram) {
        return militarRepository.findBySaram(saram).map(militarMapper::toDto);
    }
}
