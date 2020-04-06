package com.jgfx.assets.type;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.MapMaker;
import com.jgfx.assets.Asset;
import com.jgfx.assets.utils.AssetUtils;
import com.jgfx.assets.data.AssetData;
import com.jgfx.assets.data.AssetFactory;
import com.jgfx.assets.urn.ResourceUrn;
import lombok.Getter;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Semaphore;

/**
 * AssetType manages all assets of a particular type/class.  It provides the ability to resolve and load assets by Urn, and caches assets so that there is only
 * a single instance of a given asset shared by all users.
 * <p>
 * AssetType is thread safe.
 * </p>
 *
 * @param <T> The type of asset this AssetType manages
 * @param <U> The type of asset data required by the assets this AssetType manages
 */
public final class AssetType<T extends Asset<U>, U extends AssetData> {
    @Getter
    private final Class<T> assetClass;
    @Getter
    private final Class<U> assetDataClass;
    @Getter
    private final AssetFactory<T, U> factory;
    private final Map<ResourceUrn, T> loadedAssets = new MapMaker().concurrencyLevel(4).makeMap();
    // Per-asset locks to deal with situations where multiple threads attempt to obtain or create the same unloaded asset concurrently
    private final Map<ResourceUrn, ResourceLock> locks = new MapMaker().concurrencyLevel(1).makeMap();

    /**
     * Constructs an AssetType for managing assets of the provided Asset class. The Asset class must have its AssetData generic parameter bound via inheritance
     * (e.g. MyType extends Asset&lt;MyDataType&gt;)
     *
     * @param assetClass The class of asset this AssetType will manage.
     * @param factory    The factory used to convert AssetData to Assets for this type
     */
    @SuppressWarnings("unchecked")
    public AssetType(Class<T> assetClass, AssetFactory<T, U> factory) {
        Preconditions.checkNotNull(assetClass);
        Preconditions.checkNotNull(factory);

        this.factory = factory;
        this.assetClass = assetClass;
        Optional<Type> assetDataType = AssetUtils.getTypeParameterBindingForInheritedClass(assetClass, Asset.class, 0);
        if (assetDataType.isPresent()) {
            assetDataClass = (Class<U>) AssetUtils.getClassOfType(assetDataType.get());
        } else {
            throw new IllegalArgumentException("Asset class must have a bound AssetData parameter - " + assetClass);
        }
    }

    /**
     * Obtains an asset by urn, loading it if necessary. If the urn is a instance urn, then a new asset will be created from the parent asset.
     *
     * @param urn The urn of the resource to get
     * @return The asset if available
     */
    public Optional<T> getAsset(ResourceUrn urn) {
        Preconditions.checkNotNull(urn);
        return Optional.ofNullable(loadedAssets.get(urn));
    }


    /**
     * Loads an asset with the given urn and data. If the asset already exists, it is reloaded with the data instead
     *
     * @param urn  The urn of the asset        Preconditions.checkNotNull(urn);
     * @param data The data to load the asset with
     * @return The loaded (or reloaded) asset
     */
    public T loadAsset(ResourceUrn urn, AssetData data) {
        if (urn.isInstance()) {
            return factory.build(urn, this, this.getAssetDataClass().cast(data));
        } else {
            T asset = loadedAssets.get(urn);
            if (asset != null) {
                asset.reload(this.getAssetDataClass().cast(data));
            } else {
                ResourceLock lock;
                synchronized (locks) {
                    lock = locks.get(urn);
                    if (lock == null) {
                        lock = new ResourceLock(urn);
                        locks.put(urn, lock);
                    }
                }
                try {
                    lock.lock();
                    asset = loadedAssets.get(urn);
                    if (asset == null) {
                        asset = factory.build(urn, this, this.getAssetDataClass().cast(data));
                    } else {
                        asset.reload(this.getAssetDataClass().cast(data));
                    }
                    synchronized (locks) {
                        if (lock.unlock()) {
                            locks.remove(urn);
                        }
                    }
                } catch (InterruptedException e) {
                    System.err.println("Failed to load asset - interrupted awaiting lock on resource: " + urn);
                }
            }

            return asset;
        }
    }

    /**
     * @param urn The urn of the asset to check. Must not be an instance urn
     * @return Whether an asset is loaded with the given urn
     */
    public boolean isLoaded(ResourceUrn urn) {
        Preconditions.checkArgument(!urn.isInstance(), "Urn must not be an instance urn");
        return loadedAssets.containsKey(urn);
    }


    /**
     * Notifies the asset type when an asset is created
     *
     * @param baseAsset The asset that was created
     */
    public synchronized void registerAsset(Asset<U> baseAsset) {
        loadedAssets.put(baseAsset.getUrn(), assetClass.cast(baseAsset));
    }


    /**
     * @return A set of the urns of all the loaded assets.
     */
    public Set<ResourceUrn> getLoadedAssetUrns() {
        return ImmutableSet.copyOf(loadedAssets.keySet());
    }

    /**
     * @return A list of all the loaded assets.
     */
    public Set<T> getLoadedAssets() {
        return ImmutableSet.copyOf(loadedAssets.values());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof AssetType) {
            AssetType other = (AssetType) obj;
            return assetClass.equals(other.assetClass);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return assetClass.hashCode();
    }

    @Override
    public String toString() {
        return assetClass.getSimpleName();
    }


    private static final class ResourceLock {
        private final ResourceUrn urn;
        private final Semaphore semaphore = new Semaphore(1);

        public ResourceLock(ResourceUrn urn) {
            this.urn = urn;
        }

        public void lock() throws InterruptedException {
            semaphore.acquire();
        }

        public boolean unlock() {
            boolean lockFinished = !semaphore.hasQueuedThreads();
            semaphore.release();
            return lockFinished;
        }

        @Override
        public String toString() {
            return "lock(" + urn + ")";
        }
    }

}
