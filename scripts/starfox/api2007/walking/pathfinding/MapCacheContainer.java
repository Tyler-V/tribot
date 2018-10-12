package scripts.starfox.api2007.walking.pathfinding;

import java.awt.Point;
import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import scripts.starfox.api.Client;
import scripts.starfox.api.util.FileUtil;

/**
 *
 * @author Spencer
 */
public class MapCacheContainer {

    private final static int chunk_size = 128;

    private final ConcurrentHashMap<Point, MapCache> cache;

    public MapCacheContainer() {
        this.cache = new ConcurrentHashMap<>();
    }

    public final void clear() {
        for (MapCache mCache : cache.values()) {
            mCache.clear();
        }
        cache.clear();
    }

    public final boolean addData(Point point, AStarData data) {
        Point key = MapCache.getLoadingKey(point);
        MapCache mCache = cache.get(key);
        if (mCache == null) {
            Client.println("Creating new map cache (" + key.x + ", " + key.y + ")");
            mCache = new MapCache(false);
            mCache.setKey(key);
            cache.put(key, mCache);
        }
        return mCache.add(MapCache.getLoadingValue(point), data);
    }

    public final AStarData getData(Point point) {
        if (point == null) {
            throw new NullPointerException("Point cannot be null.");
        }
        final Point p = MapCache.getLoadingKey(point);
        if (cache.containsKey(p)) {
            return cache.get(p).get(MapCache.getLoadingValue(point));
        } else {
            return null;
        }
    }

    public boolean isLoaded(Point point) {
        if (point == null) {
            throw new NullPointerException("Point cannot be null.");
        }
        if (cache != null) {
            final Point p = MapCache.getLoadingKey(point);
            if (cache.containsKey(p)) {
                return cache.get(p).isLoaded(MapCache.getLoadingValue(point));
            }
        }
        return false;
    }

    public final ArrayList<AStarData> getAll() {
        final ArrayList<AStarData> all = new ArrayList<>();
        for (MapCache mCache : cache.values()) {
            all.addAll(mCache.getAll());
        }
        return all;
    }

    public final ArrayList<Point> getAllLoaded() {
        final ArrayList<Point> all = new ArrayList<>();
        for (MapCache mCache : cache.values()) {
            all.addAll(mCache.getUnloaded());
        }
        return all;
    }

    private int saveCount;

    protected final int save(final int plane) {
        saveCount = 0;
        ExecutorService pool = Executors.newFixedThreadPool(8);
        final Object lock2 = new Object();
        for (final MapCache mCache : cache.values()) {
            pool.submit(new Runnable() {
                @Override
                public void run() {
                    synchronized (lock2) {
                        lock2.notifyAll();
                    }
                    if (mCache.save(plane)) {
                        saveCount++;
                    }
                }
            });
        }
        synchronized (lock2) {
            while (!isSaved()) {
                try {
                    lock2.wait(1000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                pool.shutdown();
            }
        }
        return saveCount;
    }

    private boolean isSaved() {
        for (MapCache mCache : cache.values()) {
            if (!mCache.saved) {
                return false;
            }
        }
        return true;
    }

    protected final void load(final int plane) {
        final File[] mapFiles = FileUtil.getFilesInDirectory("cache", "Walking", "z_" + plane);
        final File[] dataFiles = FileUtil.getFilesInDirectory("cache", "Walking", "zD_" + plane);
        if (mapFiles != null && dataFiles != null) {
            int size = mapFiles.length;
            if (mapFiles.length != dataFiles.length) {
                throw new RuntimeException("File counts do not match.");
            }
            ExecutorService pool = Executors.newFixedThreadPool(8);
            //System.out.println("Files: " + Arrays.toString(mapFiles));
            for (int i = 0; i < size; i++) {
                final int i2 = i;
                pool.submit(new Runnable() {
                    @Override
                    public void run() {
                        Point p;
                        if ((p = MapCache.getKey(dataFiles[i2], cache)) != null) {
                            if (p != null) {
                                if (!p.equals(new Point(-1, -1))) {
                                    cache.remove(p);
                                }
                                MapCache mCache = new MapCache(true);
                                mCache.loadData(dataFiles[i2]);
                                mCache.loadMap(mapFiles[i2]);
                                cache.put(mCache.key, mCache);
                            }
                        }
                    }
                });
            }
            pool.shutdown();
        }
    }

    private static class MapCache {

        private final boolean[][] unloadedTiles;
        private final ConcurrentHashMap<Point, AStarData> cache;
        private Point key;
        private boolean wasLoaded;
        private boolean saved;

        private MapCache(boolean wasLoaded) {
            this.unloadedTiles = new boolean[chunk_size][chunk_size];
            if (!wasLoaded) {
                for (int i = 0; i < chunk_size; i++) {
                    for (int j = 0; j < chunk_size; j++) {
                        this.unloadedTiles[i][j] = true;
                    }
                }
            }
            this.cache = new ConcurrentHashMap<>();
            this.key = null;
            this.wasLoaded = wasLoaded;
            this.saved = false;
        }

        private void clear() {
            cache.clear();
        }

        private void setKey(Point key) {
            if (this.key == null) {
                this.key = key;
            }
        }

        private boolean add(Point p, AStarData data) {
            boolean updated = false;
            if (unloadedTiles[p.x][p.y]) {
                updated = true;
            }
            unloadedTiles[p.x][p.y] = false;
            if (!cache.containsKey(p)) {
                if (data != null) {
                    cache.put(p, data);
                }
            }
            if (updated) {
                wasLoaded = false;
            }
            return updated;
        }

        private AStarData get(Point p) {
            AStarData tempData = cache.get(p);
            if (tempData != null) {
                return tempData;
            } else {
                return null;
            }
        }

        private boolean isLoaded(Point p) {
            if (unloadedTiles != null && p != null) {
                return !unloadedTiles[p.x][p.y];
            } else {
                return false;
            }
        }

        private ArrayList<AStarData> getAll() {
            return new ArrayList<>(cache.values());
        }

        private ArrayList<Point> getUnloaded() {
            ArrayList<Point> points = new ArrayList<>();
            for (int i = 0; i < chunk_size; i++) {
                for (int j = 0; j < chunk_size; j++) {
                    if (unloadedTiles[i][j]) {
                        points.add(new Point((key.x * chunk_size) + i, (key.y * chunk_size) + j));
                    }
                }
            }
            return points;
        }

        private boolean save(int plane) {
            if (!wasLoaded) {
                String text = "";
                ArrayList<AStarData> datas = getAll();
                for (AStarData data : datas) {
                    if (!data.preloaded) {
                        text += data.getX() + ":";
                        text += data.getY() + ":";
                        text += data.getPlane() + ":";
                        text += data.getCollisionData() + ":";
                        text += (data.isBlocked() ? "1" : "0") + ":";
                        text += (data.containsDoor() ? "1" : "0") + ":";
                        text += (data.containsObstacle() ? "1" : "0") + ":";
                        text += (data.blockedN ? "1" : "0") + ":";
                        text += (data.blockedS ? "1" : "0") + ":";
                        text += (data.blockedE ? "1" : "0") + ":";
                        text += (data.blockedW ? "1" : "0") + "\n";
                    }
                }
                FileUtil.writeTextFileContents(text, "Map " + key.x + "_" + key.y, "cache", false, "Walking", "z_" + plane);
                text = "";
                text += key.x + ":";
                text += key.y + "\n";
                for (int i = 0; i < unloadedTiles.length; i++) {
                    for (int j = 0; j < unloadedTiles[i].length; j++) {
                        if (unloadedTiles[i][j]) {
                            text += i + ":";
                            text += j + "\n";
                        }
                    }
                }
                FileUtil.writeTextFileContents(text, "Map Data " + key.x + "_" + key.y, "cache", true, "Walking", "zD_" + plane);
            }
            saved = true;
            return !wasLoaded;
        }

        private void loadData(File file) {
            FileUtil.TextTraversable traversable = new FileUtil.TextTraversable() {
                @Override
                public void traverseNext(String next, int lineNumber) {
                    if (!next.isEmpty()) {
                        String[] keys = next.split(":");
                        int x1 = parseInt(keys, 0);
                        int y1 = parseInt(keys, 1);
                        if (lineNumber == 1) {
                            setKey(new Point(x1, y1));
                        } else if (lineNumber >= 2) {
                            unloadedTiles[x1][y1] = true;
                        }
                    }
                }
            };
            FileUtil.traverseTextFile(traversable, file);
        }

        private void loadMap(File file) {
            FileUtil.TextTraversable traversable = new FileUtil.TextTraversable() {
                @Override
                public void traverseNext(String next, int lineNumber) {
                    if (!next.isEmpty()) {
                        String[] keys = next.split(":");
                        int x1 = parseInt(keys, 0);
                        int y1 = parseInt(keys, 1);
                        int plane2 = parseInt(keys, 2);
                        add(getLoadingValue(new Point(x1, y1)), new AStarData(
                                x1, y1, plane2,
                                parseInt(keys, 3), parseBool(keys, 4), parseBool(keys, 5), parseBool(keys, 6),
                                parseBool(keys, 7), parseBool(keys, 8), parseBool(keys, 9), parseBool(keys, 10), true));
                    }
                }
            };
            FileUtil.traverseTextFile(traversable, file);
        }

        private static Point loadingKey;

        private static Point getKey(final File file, final ConcurrentHashMap<Point, MapCache> superCache) {
            loadingKey = null;
            FileUtil.TextTraversable traversable = new FileUtil.TextTraversable() {
                @Override
                public void traverseNext(String next, int lineNumber) {
                    if (loadingKey == null && !next.isEmpty()) {
                        String[] keys = next.split(":");
                        int x1 = parseInt(keys, 0);
                        int y1 = parseInt(keys, 1);
                        MapCache mCache = superCache.get(new Point(x1, y1));
                        if (mCache == null || mCache.wasLoaded) {
                            loadingKey = mCache != null ? mCache.key : new Point(-1, -1);
                        }
                    }
                }
            };
            FileUtil.traverseTextFile(traversable, file);
            return loadingKey;
        }

        private static int parseInt(String[] nexts, int index) {
            return nexts != null && nexts.length != 0 ? Integer.parseInt(nexts[index]) : -1;
        }

        private static boolean parseBool(String[] nexts, int index) {
            int bool = parseInt(nexts, index);
            return nexts != null && nexts.length != 0 ? bool == 1 : false;
        }

        private static Point getLoadingKey(Point p) {
            return new Point(p.x / chunk_size, p.y / chunk_size);
        }

        private static Point getLoadingValue(Point p) {
            return new Point(p.x % chunk_size, p.y % chunk_size);
        }
    }
}
