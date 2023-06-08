import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Iterator;

import javax.imageio.*;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

public class Main {

    public static void main(String[] args) {
        String pdfFilePath = "/Users/gaebabja/IdeaProjects/algorithm/src/main/resources/PDF/test1.pdf";
        String outputFilePath = "/Users/gaebabja/IdeaProjects/algorithm/src/main/resources/TIFF/test1.tif";

        try {
            Main converter = new Main();
            converter.convertPdfToTiff(pdfFilePath, outputFilePath);
            System.out.println("PDF to TIFF conversion completed successfully.");
        } catch (Exception e) {
            System.err.println("Failed to convert PDF to TIFF: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void convertPdfToTiff(String pdfFilePath, String outputFilePath) throws Exception {
        try (PDDocument document = PDDocument.load(new File(pdfFilePath))) {
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            int pageCount = document.getNumberOfPages();

            BufferedImage[] images = new BufferedImage[pageCount];

            for (int page = 0; page < pageCount; page++) {
                BufferedImage image = pdfRenderer.renderImageWithDPI(page, 100, ImageType.RGB);
                images[page] = image;
            }

            ImageWriter writer = null;
            ImageOutputStream output = null;
            try {
                Iterator<ImageWriter> writerIter = ImageIO.getImageWritersByFormatName("TIFF");
                System.out.println(writerIter.hasNext());
                writer = writerIter.next();

                ImageWriteParam param = writer.getDefaultWriteParam();
                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                param.setCompressionType("Deflate");

                File outputfile = new File(outputFilePath);
                output = new FileImageOutputStream(outputfile);
                writer.setOutput(output);

                IIOMetadata metadata = writer.getDefaultImageMetadata(new ImageTypeSpecifier(images[0]), param);
                writer.prepareWriteSequence(metadata);

                // TIFF 이미지로 변환
                for (int page = 0; page < pageCount; page++) {
                    BufferedImage image = images[page];
                    IIOImage iioImage = new IIOImage(image, null, null);
                    writer.writeToSequence(iioImage, param);
                }

                // 이미지 쓰기 종료
                writer.endWriteSequence();

            } finally {
                if (writer != null) {
                    writer.dispose();
                }
                if (output != null) {
                    output.close();
                }
            }
        }
    }
}
