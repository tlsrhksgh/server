package com.example.server.common.entity;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.Expressions;

import java.time.LocalDateTime;

public class DateFormatExpression {

    //mysql expression
    public static Expression<String> formatDateTime(DateTimePath<LocalDateTime> date) {
        return Expressions.stringTemplate(
                "DATE_FORMAT({0}, '%y-%m-%d %H:%i:%s')",
                date
        );
    }
}
