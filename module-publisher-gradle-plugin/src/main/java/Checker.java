import org.gradle.api.Project;

class Checker {
    static boolean isApk(Project project) {
        return project.getPlugins().hasPlugin("com.android.application");
    }

    static boolean isAar(Project project) {
        return project.getPlugins().hasPlugin("com.android.library");
    }

    static boolean isJar(Project project) {
        return project.getPlugins().hasPlugin("java-library");
    }

    static boolean isJava(Project project) {
        return project.getPlugins().hasPlugin("java");
    }

    static boolean isKotlin(Project project) {
        return project.getPlugins().hasPlugin("kotlin");
    }

    static void checkMavenLocal(PublishExtension extension) {
        check(extension);
    }

    static void checkMavenCentralRelease(PublishExtension extension) {
        checkMavenCentralSnapshots(extension);
        if (isMavenCentralSigningEmpty(extension)) {
            throw new NullPointerException("U should set signingKeyId/signingPassword/signingSecretKeyRingFile in local.properties");
        }

    }

    static void checkMavenCentralSnapshots(PublishExtension extension) {
        check(extension);
        if (isMavenCentralAccountEmpty(extension)) {
            throw new NullPointerException("U should set ossrhUsername/ossrhPassword in local.properties");
        }
    }

    static void check(PublishExtension extension) {
        checkField(extension.name, "name");
        checkField(extension.groupId, "groupId");
        checkField(extension.artifactId, "artifactId");
        checkField(extension.version, "version");
        checkField(extension.website, "website");
    }

    static boolean isMavenCentralSigningEmpty(PublishExtension extension) {
        return isEmpty(extension.signingKeyId) || isEmpty(extension.signingSecretKeyRingFile) || isEmpty(extension.signingPassword);
    }

    static boolean isMavenCentralAccountEmpty(PublishExtension extension) {
        return isEmpty(extension.ossrhUsername) || isEmpty(extension.ossrhPassword);
    }

    static void checkField(String field, String fieldName) {
        if (isEmpty(field)) {
            throw new NullPointerException(fieldName + " is empty!!");
        }
    }

    static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }
}