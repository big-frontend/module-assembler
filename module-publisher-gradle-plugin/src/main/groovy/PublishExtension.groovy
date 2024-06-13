class PublishExtension {
    String name
    String groupId
    String artifactId
    String version
    String artifactPath
    String website
    String buildVariant //只创建指定的变种发布task

    String ossrhUsername
    String ossrhPassword
    String releasesRepoUrl
    String snapshotsRepoUrl

    String signingKeyId
    String signingSecretKeyRingFile
    String signingPassword
}