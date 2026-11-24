package com.zl.zlaiagent.tools;

import cn.hutool.core.io.FileUtil;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.zl.zlaiagent.constant.FileConstant;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * PDF 生成工具
 */
public class PDFGenerationTool {

    /** Windows 常见中文字体候选（优先使用系统字体，避免依赖内置 CID 字体） */
    private static final String[] WINDOWS_CJK_FONTS = {
            "C:/Windows/Fonts/msyh.ttc,0",
            "C:/Windows/Fonts/simsun.ttc,0",
            "C:/Windows/Fonts/simhei.ttf",
            "C:/Windows/Fonts/STSONG.TTF"
    };

    @Tool(description = "Generate a PDF file with given content", returnDirect = false)
    public String generatePDF(
            @ToolParam(description = "Name of the file to save the generated PDF") String fileName,
            @ToolParam(description = "Content to be included in the PDF") String content) {
        String fileDir = FileConstant.FILE_SAVE_DIR + "/pdf";
        String filePath = fileDir + "/" + fileName;
        try {
            FileUtil.mkdir(fileDir);
            try (PdfWriter writer = new PdfWriter(filePath);
                 PdfDocument pdf = new PdfDocument(writer);
                 Document document = new Document(pdf)) {
                PdfFont font = createCjkCapableFont();
                document.setFont(font);
                document.add(new Paragraph(content));
            }
            return "PDF generated successfully to: " + filePath;
        } catch (IOException e) {
            return "Error generating PDF: " + e.getMessage();
        }
    }

    /**
     * 按优先级创建可显示中文的字体：系统字体 → Adobe 亚洲字体 → Helvetica（仅英文）
     */
    private PdfFont createCjkCapableFont() throws IOException {
        for (String fontPath : WINDOWS_CJK_FONTS) {
            String fileOnly = fontPath.contains(",") ? fontPath.substring(0, fontPath.indexOf(',')) : fontPath;
            if (Files.exists(Path.of(fileOnly))) {
                try {
                    return PdfFontFactory.createFont(
                            fontPath,
                            PdfEncodings.IDENTITY_H,
                            PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);
                } catch (Exception ignored) {
                    // try next candidate
                }
            }
        }
        try {
            return PdfFontFactory.createFont("STSongStd-Light", "UniGB-UCS2-H");
        } catch (Exception ignored) {
            // last resort for Latin-only content
            return PdfFontFactory.createFont();
        }
    }
}
