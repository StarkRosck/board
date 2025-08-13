package com.board.board.persistence.migration;

import liquibase.Liquibase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.sql.Connection;

public class MigrationStrategy {

    private final Connection connection;

    public MigrationStrategy(Connection connection) {
        this.connection = connection;
    }

    /** Executa as migrações do Liquibase usando a conexão fornecida. */
    public void executeMigration() {
        PrintStream originalOut = System.out;
        PrintStream originalErr = System.err;

        try (FileOutputStream fos = new FileOutputStream("liquibase.log");
             PrintStream ps = new PrintStream(fos)) {

            // redireciona logs do Liquibase para arquivo (opcional)
            System.setOut(ps);
            System.setErr(ps);

            // caminho padrão no classpath (sem barra inicial)
            final String changelog = "db/changelog/db.changelog-master.yaml";

            // sanity check: garante que o changelog está no classpath
            if (Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream(changelog) == null) {
                throw new IllegalStateException("Changelog não encontrado no classpath: " + changelog);
            }

            try (JdbcConnection jdbc = new JdbcConnection(connection)) {
                Liquibase liquibase = new Liquibase(
                        changelog,
                        new ClassLoaderResourceAccessor(),
                        jdbc
                );
                liquibase.update((String) null); // contexts = null
            }
        } catch (LiquibaseException | RuntimeException | java.io.IOException e) {
            e.printStackTrace(originalErr);
        } finally {
            System.setOut(originalOut);
            System.setErr(originalErr);
        }
    }
}
