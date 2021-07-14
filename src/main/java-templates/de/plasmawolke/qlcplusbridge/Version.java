package de.plasmawolke.qlcplusbridge;

import org.apache.commons.lang3.StringUtils;

public final class Version {

    private static final String VERSION = "${project.version}";
    private static final String REVISION = "${buildNumber}";

    public static String getVersion() {
        return VERSION;
    }

    public static String getRevision() {
        return REVISION;
    }

    public static String getVersionAndRevision(){
        return Version.getVersion() + " (rev."+ StringUtils.substring(Version.getRevision(),0,7)+")";
    }

}
