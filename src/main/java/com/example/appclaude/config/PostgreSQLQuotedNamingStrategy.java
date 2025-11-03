package com.example.appclaude.config;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

/**
 * Custom naming strategy that adds quotes to identifiers for PostgreSQL
 * to preserve case sensitivity when column names use mixed case.
 */
public class PostgreSQLQuotedNamingStrategy implements PhysicalNamingStrategy {

    @Override
    public Identifier toPhysicalCatalogName(Identifier name, JdbcEnvironment jdbcEnvironment) {
        return quoteIdentifier(name);
    }

    @Override
    public Identifier toPhysicalSchemaName(Identifier name, JdbcEnvironment jdbcEnvironment) {
        return quoteIdentifier(name);
    }

    @Override
    public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment jdbcEnvironment) {
        return quoteIdentifier(name);
    }

    @Override
    public Identifier toPhysicalSequenceName(Identifier name, JdbcEnvironment jdbcEnvironment) {
        return quoteIdentifier(name);
    }

    @Override
    public Identifier toPhysicalColumnName(Identifier name, JdbcEnvironment jdbcEnvironment) {
        return quoteIdentifier(name);
    }

    private Identifier quoteIdentifier(Identifier name) {
        if (name == null) {
            return null;
        }
        // If the identifier is already quoted, return as is
        // Otherwise, quote it to preserve case sensitivity
        if (name.isQuoted()) {
            return name;
        }
        // Quote the identifier to preserve case for PostgreSQL
        return Identifier.toIdentifier(name.getText(), true);
    }
}

