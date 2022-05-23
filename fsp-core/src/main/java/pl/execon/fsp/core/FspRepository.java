package pl.execon.fsp.core;

public interface FspRepository<T> {

    FspResponse<T> find(FspRequest request);
}
