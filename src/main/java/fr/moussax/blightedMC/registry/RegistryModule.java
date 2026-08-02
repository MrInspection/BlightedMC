package fr.moussax.blightedMC.registry;

/**
 * Universal functional interface for registry modules in BlightedMC.
 *
 * @param <T> the type of registry consumer or handler this module accepts
 */
@FunctionalInterface
public interface RegistryModule<T> {
    void register(T registry);
}
