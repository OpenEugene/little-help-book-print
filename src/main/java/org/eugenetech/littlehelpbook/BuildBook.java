package org.eugenetech.littlehelpbook;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.html2pdf.resolver.font.DefaultFontProvider;
import com.itextpdf.layout.font.FontProvider;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.eugenetech.cli.Base;
import org.eugenetech.littlehelpbook.airtable.Airtable;
import org.eugenetech.littlehelpbook.data.AirtableRecord;
import org.eugenetech.littlehelpbook.data.Repository;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Slf4j
public class BuildBook extends Base {
    final static TemplateEngine engine = new TemplateEngine();

    static {
        final ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setTemplateMode(TemplateMode.HTML);

        templateResolver.setPrefix("templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setCharacterEncoding("UTF-8");
        templateResolver.setCacheable(true);

        engine.setTemplateResolver(templateResolver);
    }

    @Override
    protected void execute() throws Exception {

        final Repository repository = new Repository();
        final Airtable airtable = new Airtable(this.getProperty("airtable.endpoint"), this.getProperty("airtable.token"));

        for (final AirtableRecord record : airtable.getAll("Cities")) {
            repository.addCity(record);
        }

        for (final AirtableRecord record : airtable.getAll("Categories")) {
            repository.addCategory(record);
        }

        for (final AirtableRecord record : airtable.getAll("Subcategories")) {
            repository.addSubcategory(record);
        }

        for (final AirtableRecord record : airtable.getAll("CatSubcats")) {
            repository.addCatSubcat(record);
        }

        for (final AirtableRecord record : airtable.getAll("Help%20Services")) {
            repository.addResource(record);
        }

        final Context english = new Context();
        english.setLocale(Locale.forLanguageTag("en-US"));
        english.setVariable("repository", repository);

        final Context spanish = new Context();
        spanish.setLocale(Locale.forLanguageTag("es-MX"));
        spanish.setVariable("repository", repository);

        repository.writeInfo();

        final File path = new File(this.getProperty("output.path"));

        try {
            final File englishFile = new File(path, "english.html");
            final File spanishFile = new File(path, "spanish.html");
//            final File englishFilePDF = new File(path, "english.pdf");
//            final File spanishFilePDF = new File(path, "spanish.pdf");

            @Cleanup final FileWriter englishWriter = new FileWriter(englishFile);
            @Cleanup final FileWriter spanishWriter = new FileWriter(spanishFile);

            engine.process("english", english, englishWriter);
            engine.process("spanish", spanish, spanishWriter);

            englishWriter.close();
            spanishWriter.close();

            final ConverterProperties converterProperties = new ConverterProperties();
            converterProperties.setFontProvider(new DefaultFontProvider(true, true, true));
//            HtmlConverter.convertToPdf(new FileInputStream(englishFile), new FileOutputStream(englishFilePDF), converterProperties);
//            HtmlConverter.convertToPdf(new FileInputStream(spanishFile), new FileOutputStream(spanishFilePDF), converterProperties);
        } catch (final Exception e) {
            throw e;
        }

        try {
            @Cleanup final FileWriter englishWordWriter = new FileWriter(new File(path, "english.word.html"));
            @Cleanup final FileWriter spanishWordWriter = new FileWriter(new File(path, "spanish.word.html"));

            engine.process("english.word", english, englishWordWriter);
            engine.process("spanish.word", spanish, spanishWordWriter);
        } catch (final Exception e) {
            throw e;
        }
    }

    public static void main(String[] args) throws Exception {
        (new BuildBook()).execute(args);
    }

    @Override
    protected Map<String, String> requiredArgs() {
        Map<String, String> args = new HashMap<>();

        args.put("airtable.endpoint", "The Airtable base endpoint");
        args.put("airtable.token", "The Airtable security token");

        args.put("output.path", "A path to the intended HTML output");

        return args;
    }

    @Override
    protected Map<String, String> optionalArgs() {
        Map<String, String> args = new HashMap<>();

        return args;
    }

    @Override
    protected Map<String, String> defaultValues() {
        Map<String, String> args = new HashMap<>();

        return args;
    }
}
