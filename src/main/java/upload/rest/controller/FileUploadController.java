package upload.rest.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.apache.tika.config.TikaConfig;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.ok;

@RestController
public class FileUploadController {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    //TODO - fix - file will be dumped onto the classpath
    private final String filePrefix = "./target/classes/main/";

    @RequestMapping(value="/upload", method=RequestMethod.POST)
    public ResponseEntity<String> handleFileUpload2(@RequestParam("name") final String name,
                                                    @RequestParam("file") final MultipartFile file){
        final String error;

        if (null != file && !file.isEmpty()) {
            try {

                byte[] bytes = file.getBytes();
                final String filePath = filePrefix + name;
                File outfile = new File(filePath);
                BufferedOutputStream stream =
                        new BufferedOutputStream(new FileOutputStream(outfile));
                stream.write(bytes);
                String actualFileType = getActualFileExtension(outfile);
                stream.close();
                final URI uri = linkTo(methodOn(this.getClass()).getFile(name)).toUri();
                final String downloadUri = "<a href=\"" + uri + "\" >" + name + "<a/>";
                return ok().body("Uploaded [" + downloadUri + "] <br /><br />" + actualFileType);
//                return ok().body("Uploaded [" + name + "]");
            } catch (Exception e) {
                error = "Failed to upload [" + name + "], " + e.getMessage();
                log.error(error);
                return badRequest().body(error);
            }
        } else {
            error = "Failed to upload [" + name + "], empty file";
            log.error(error);
            return badRequest().body(error);
        }
    }


    //See http://stackoverflow.com/a/20743941 for reason behind {filename:.+}
    @RequestMapping(value = "/download/{filename:.+}", method = RequestMethod.GET)
    public ResponseEntity<InputStreamResource> getFile(@PathVariable("filename") final String fileName) throws IOException {
        ClassPathResource fileToReturn = new ClassPathResource(fileName);
        //prevent caching
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        return ResponseEntity
                .ok()
                .headers(headers)
                .contentLength(fileToReturn.contentLength())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(new InputStreamResource(fileToReturn.getInputStream()));
    }


    //TODO - FIX
    public String getActualFileExtension(File file) throws IOException, MimeTypeException {
        TikaConfig config = TikaConfig.getDefaultConfig();
        InputStream stream = new BufferedInputStream(new FileInputStream(file));
        org.apache.tika.mime.MediaType mediaType = config.getMimeRepository().detect(stream, new Metadata());
        String mediaTypeString = mediaType.toString();
        MimeType mimeType = config.getMimeRepository().forName(mediaTypeString);
        String details = "type [" + mediaTypeString + "] extension [" + mimeType.getExtension() + "]";

        return details;
    }

    //TODO - validate file length
    //TODO - validate file size (see limits in application.yml)
    // REF - https://www.owasp.org/index.php/Unrestricted_File_Upload

}
