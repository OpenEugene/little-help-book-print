package org.eugenetech.littlehelpbook;

import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.eugenetech.cli.Base;
import org.eugenetech.littlehelpbook.airtable.Airtable;
import org.eugenetech.littlehelpbook.data.AirtableRecord;
import org.eugenetech.littlehelpbook.data.Repository;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.IContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.File;
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

        @Cleanup final FileWriter englishWriter = new FileWriter(new File(path, "english.html"));
        @Cleanup final FileWriter spanishWriter = new FileWriter(new File(path, "spanish.html"));

        engine.process("english", english, englishWriter);
        engine.process("spanish", spanish, spanishWriter);
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
