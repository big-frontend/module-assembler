package com.qihoo360.replugin.util


import java.util.zip.ZipFile

class F {
    static void zip(File srcFile/*dir or jar*/, File destZip) {
        if (!destZip.exists()) {
            destZip.mkdirs()
        }
//        ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(destZip))
//        JarOutputStream jarOut = new JarOutputStream(new FileOutputStream(destJar))
//        jarOut.close()
//        zipOut.close()
    }

    static List<File> unzip(File zipFile, File destDir) {
        if (!destDir.exists()) {
            destDir.mkdirs()
        }
        List<File> unzipDir = new ArrayList<>()
        new ZipFile(zipFile).withCloseable { theZip ->
            theZip.entries().each { zipEntry ->
                File file = new File(destDir, zipEntry.name)
                unzipDir.add(file)
                if (zipEntry.directory) {
                    createOrExistsDir(file)
                    return
                }
//                println("cjf ${zipEntry.name}  ${file}")
                createOrExistsFile(file)
                file.withOutputStream { fout ->
                    fout.write(theZip.getInputStream(zipEntry).bytes)
                }
            }
        }
        return unzipDir
    }

    private static boolean createOrExistsDir(final File file) {
        return file != null && (file.exists() ? file.isDirectory() : file.mkdirs());
    }

    private static boolean createOrExistsFile(final File file) {
        if (file == null) return false;
        if (file.exists()) return file.isFile();
        if (!createOrExistsDir(file.getParentFile())) return false;
        try {
            return file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}