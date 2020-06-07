package vn.candicode.payloads.transformers;

public interface GenericTransformer<F, T> {
    T transform(F o);
}
