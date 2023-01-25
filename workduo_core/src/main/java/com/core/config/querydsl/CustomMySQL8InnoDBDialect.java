package com.core.config.querydsl;

import org.hibernate.dialect.MySQL8Dialect;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.type.StandardBasicTypes;

public class CustomMySQL8InnoDBDialect extends MySQL8Dialect {

    public CustomMySQL8InnoDBDialect() {
        super();

        this.registerFunction("DATE_FORMAT", new StandardSQLFunction("DATE_FORMAT", StandardBasicTypes.STRING));
        this.registerFunction("TIMESTAMPDIFF", new StandardSQLFunction("TIMESTAMPDIFF", StandardBasicTypes.INTEGER));
        this.registerFunction("ANY_VALUE", new StandardSQLFunction("TIMESTAMPDIFF", StandardBasicTypes.STRING));
    }
}
