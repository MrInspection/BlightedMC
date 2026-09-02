package fr.moussax.blightedSMP.registry;

/**
 * Universal functional interface for registry modules in BlightedMC.
 *
 * @param <T> the type of registry consumer or handler this module accepts
 */
@FunctionalInterface
public interface RegistryModule<T> {

    /**
     * Registers content with the provided registry consumer or handler.
     *
     * @param registry consumer or handler receiving the registered content
     */
    void register(T registry);
}
