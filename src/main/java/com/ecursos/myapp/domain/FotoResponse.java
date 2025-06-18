package com.ecursos.myapp.domain;

public class FotoResponse {
    private String tpArq;
    private String txNomeArq;
    private String txTamanhoArq;
    private String imFoto;

    // Getters e Setters
    public String getTpArq() {
        return tpArq;
    }

    public void setTpArq(String tpArq) {
        this.tpArq = tpArq;
    }

    public String getTxNomeArq() {
        return txNomeArq;
    }

    public void setTxNomeArq(String txNomeArq) {
        this.txNomeArq = txNomeArq;
    }

    public String getTxTamanhoArq() {
        return txTamanhoArq;
    }

    public void setTxTamanhoArq(String txTamanhoArq) {
        this.txTamanhoArq = txTamanhoArq;
    }

    public String getImFoto() {
        return imFoto;
    }

    public void setImFoto(String imFoto) {
        this.imFoto = imFoto;
    }
}
