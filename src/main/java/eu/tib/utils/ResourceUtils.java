package eu.tib.utils;

import org.apache.commons.io.FilenameUtils;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class ResourceUtils {

    public String readResourceNoWhitespace(String filename) throws IOException {
        return readResource(filename, "\n")
                .replaceAll("\\s+", " ");
    }

    public String readResource(String filename) throws IOException {
        return readResource(filename, "\n");
    }

    public String readResource(String filename, String delimiter) throws IOException {

        try (InputStream inputStream = getClass().getResourceAsStream(filename);
             InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(streamReader);) {

            String content = reader.lines().collect(Collectors.joining(delimiter));
            return content;
        }
    }

    public File resource2File(String resPath) throws IOException {
        URL res = getClass().getResource(resPath);

        if (res.toString().startsWith("jar:")) {
            try (InputStream input = getClass().getResourceAsStream(resPath);) {

                String basename = FilenameUtils.getBaseName(resPath);
                String extension = FilenameUtils.getExtension(resPath);
                File file = File.createTempFile(basename, "." + extension);

                OutputStream out = new FileOutputStream(file);
                int read;
                byte[] bytes = new byte[1024];

                while ((read = input.read(bytes)) != -1) {
                    out.write(bytes, 0, read);
                }
                out.flush();
                out.close();
                file.deleteOnExit();
                return file;
            }
        } else {
            return new File(res.getFile());
        }
    }
}
