function setupProxy({ tls }) {
  const serverResources = ['/api', '/services', '/management', '/v3/api-docs', '/h2-console', '/auth', '/oauth2', '/login', '/health', '/webservice' ];
  return [
    {
      context: serverResources,
      target: `http${tls ? 's' : ''}://localhost:8080`,
      secure: false,
      changeOrigin: tls,
    },
    {
      context: ['/webservice'], // Configuração adicional para direcionar o proxy ao seu webservice
      target: 'http://172.16.44.40',
      secure: false,
      changeOrigin: true,
    },
  ];
}

module.exports = setupProxy;
