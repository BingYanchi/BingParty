/*
 * Decompiled with CFR 0.151.
 */
package org.inventivetalent.update.spiget.bukkit.comparator;

public abstract class VersionComparator {
    public static final VersionComparator EQUAL = new VersionComparator(){

        @Override
        public boolean isNewer(String currentVersion, String checkVersion) {
            return !currentVersion.equals(checkVersion);
        }
    };
    public static final VersionComparator SEM_VER = new VersionComparator(){

        @Override
        public boolean isNewer(String currentVersion, String checkVersion) {
            currentVersion = currentVersion.replace(".", "");
            checkVersion = checkVersion.replace(".", "");
            try {
                int current = Integer.parseInt(currentVersion);
                int check = Integer.parseInt(checkVersion);
                return check > current;
            }
            catch (NumberFormatException e) {
                System.err.println("[SpigetUpdate] Invalid SemVer versions specified [" + currentVersion + "] [" + checkVersion + "]");
                return false;
            }
        }
    };
    public static final VersionComparator SEM_VER_SNAPSHOT = new VersionComparator(){

        @Override
        public boolean isNewer(String currentVersion, String checkVersion) {
            currentVersion = currentVersion.replace("-SNAPSHOT", "");
            checkVersion = checkVersion.replace("-SNAPSHOT", "");
            return SEM_VER.isNewer(currentVersion, checkVersion);
        }
    };

    public abstract boolean isNewer(String var1, String var2);
}

