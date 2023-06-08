import com.github.jaiimageio.impl.plugins.tiff.TIFFImageWriterSpi;

import com.github.jaiimageio.plugins.tiff.TIFFImageWriteParam;
import com.sun.media.jai.codec.TIFFEncodeParam;
import com.sun.media.jai.codec.TIFFField;
import com.sun.media.jai.codecimpl.TIFFImageEncoder;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import static javax.imageio.ImageWriteParam.MODE_EXPLICIT;

public class Main1 {
    public static void main(String[] args) {

        // PDF 파일 경로
        String pdfFilePath = "/Users/gaebabja/IdeaProjects/algorithm/src/main/resources/PDF/test1.pdf";

        // TIF 파일 경로
        String tifFilePath = "/Users/gaebabja/IdeaProjects/algorithm/src/main/resources/TIFF/test1.tif";

        try (PDDocument document = PDDocument.load(new File(pdfFilePath))) {
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            int pageCount = document.getNumberOfPages();
            RenderedImage[] images = new RenderedImage[pageCount];
            for (int i = 0; i < pageCount; i++) {
                BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(i, 100, ImageType.GRAY);
                images[i] = bufferedImage;
            }


            // TIFF 파일로 저장
            String outputFilePath = tifFilePath;
            File outputFile = new File(outputFilePath);
            ImageWriter writer = ImageIO.getImageWritersByFormatName("TIFF").next();
            ImageTypeSpecifier imageTypeSpecifier = ImageTypeSpecifier.createFromRenderedImage(images[0]);
            IIOMetadata metadata = writer.getDefaultImageMetadata(imageTypeSpecifier, null);
            TIFFImageWriteParam param = new TIFFImageWriteParam(null);

            param.setCompressionMode(TIFFImageWriteParam.MODE_EXPLICIT);
            param.setCompressionType("ZLib");
            param.setCompressionQuality(0.1f);
            //타일링은 이미지를 작은 블록들로 나누어 저장하는 방식, I/O 성능을 향상
            param.setTilingMode(TIFFImageWriteParam.MODE_DISABLED);

            FileImageOutputStream outputStream = new FileImageOutputStream(outputFile);
            writer.setOutput(outputStream);
            writer.prepareWriteSequence(null);

            for (int i = 0; i < images.length; i++) {
                IIOImage iioImage = new IIOImage(images[i], null, metadata);
                writer.writeToSequence(iioImage, param);
            }

            writer.endWriteSequence();
            outputStream.close();
            System.out.println("이미지 합성 완료: " + outputFilePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}