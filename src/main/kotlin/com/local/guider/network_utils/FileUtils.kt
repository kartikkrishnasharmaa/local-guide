package com.local.guider.network_utils

import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.attribute.PosixFilePermission

object FileUtils {

    private val ROOT_DIRECTORY = System.getProperty("user.dir") + "/appservers/apache-tomcat-1x/webapps"
    const val FILES_DIRECTORY = "Uploads/LocalGuider"

    private fun userFileDirectory(dir: String): File {
        val userDir = File("$ROOT_DIRECTORY/$FILES_DIRECTORY/$dir")
        if (!userDir.exists()) {
            userDir.mkdirs()
            Files.setPosixFilePermissions(
                userDir.toPath(),
                setOf(
                    PosixFilePermission.OWNER_READ,
                    PosixFilePermission.OWNER_WRITE,
                    PosixFilePermission.OWNER_EXECUTE,
                    PosixFilePermission.GROUP_READ,
                    PosixFilePermission.GROUP_EXECUTE,
                    PosixFilePermission.OTHERS_READ,
                    PosixFilePermission.OTHERS_EXECUTE
                )
            )
        }
        return userDir
    }

//    fun saveImage(dir: String, file: MultipartFile): String? {
//        return try {
//            val profileImage = File(userFileDirectory(dir), "/${file.originalFilename}")
//            val fOut = FileOutputStream(profileImage)
//            fOut.write(file.bytes)
//            fOut.close()
//            Files.setPosixFilePermissions(
//                profileImage.toPath(), setOf(
//                    PosixFilePermission.OWNER_READ,
//                    PosixFilePermission.OWNER_WRITE,
//                    PosixFilePermission.GROUP_READ,
//                    PosixFilePermission.OTHERS_READ
//                )
//            )
//            "$FILES_DIRECTORY/$dir/${file.originalFilename}"
//        } catch (e: Exception) {
//            throw Exception(e)
//            e.printStackTrace()
//            null
//        }
//    }

    @Throws
    fun saveImage(dir: String, file: MultipartFile, extension: String? = null): String {
        val fileName = "image_${System.currentTimeMillis()}.${extension ?: "jpg"}"
        val profileImage = File(userFileDirectory("files"), fileName)
        val fOut = FileOutputStream(profileImage)
        fOut.write(file.bytes)
        fOut.close()
        Files.setPosixFilePermissions(
            profileImage.toPath(), setOf(
                PosixFilePermission.OWNER_READ,
                PosixFilePermission.OWNER_WRITE,
                PosixFilePermission.GROUP_READ,
                PosixFilePermission.OTHERS_READ
            )
        )
        return fileName
    }

    fun getImage(path: String): File? {
        return try {
            return File("$ROOT_DIRECTORY/$path")
        } catch (e: Exception) {
            null
        }
    }

}