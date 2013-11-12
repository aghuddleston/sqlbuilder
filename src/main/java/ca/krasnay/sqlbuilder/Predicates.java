package ca.krasnay.sqlbuilder;

import java.util.Arrays;
import java.util.List;

/**
 * Collection of commonly used predicates, implemented by static methods.
 *
 * <p>
 * Most predicates accept a SQL expression and one or more values to which the
 * SQL is compared. Predicates do not escape this expression. As such, do not
 * accept arbitrary expressions from users or other sources, as it may be a
 * source of SQL injection vulnerabilities. The normal use-case is that these
 * expressions are hard-coded in your application. Values, on the other hand,
 * are substituted as proper prepared statement parameters so they are safe
 * from SQL injection.
 *
 * @author <a href="mailto:john@krasnay.ca">John Krasnay</a>
 */
public final class Predicates {

    /**
     * Joins a series of predicates with AND.
     */
    public static Predicate and(Predicate... predicates) {
        return join("and", Arrays.asList(predicates));
    }

    /**
     * Joins a series of predicates with AND.
     */
    public static Predicate and(List<Predicate> predicates) {
        return join("and", predicates);
    }

    /**
     * Adds an equals clause to a creator.
     *
     * @param expr
     *            SQL expression to be compared for equality.
     * @param value
     *            Value to which the SQL expression is compared.
     */
    public static Predicate eq(final String expr, final Object value) {
        return new Predicate() {
            private String param;
            public void init(AbstractSqlCreator creator) {
                param = creator.allocateParameter();
                creator.setParameter(param, value);
            }
            public String toSql() {
                return String.format("%s = :%s", expr, param);
            }
        };
    }
    
    public static Predicate like(final String expr, final Object value) {
        return new Predicate() {
            private String param;
            public void init(AbstractSqlCreator creator) {
                param = creator.allocateParameter();
                creator.setParameter(param, value);
            }
            public String toSql() {
                return String.format("%s like :%s", expr, param);
            }
        };
    }
    
    public static Predicate isNull(final String expr) {
    	return new Predicate() {
            public void init(AbstractSqlCreator creator) {
            }
            public String toSql() {
                return String.format("%s is null", expr);
            }
    	};
    }

    public static Predicate like(final String expr, final String value) {
    	return new Predicate() {
    		private String param;
            public void init(AbstractSqlCreator creator) {
                param = creator.allocateParameter();
                creator.setParameter(param, value);
            }
            public String toSql() {
                return String.format("%s like :%s", expr, param);
            }
    	};
    }
    
    /**
     * Adds an IN clause to a creator.
     *
     * @param expr
     *            SQL expression to be tested for inclusion.
     * @param values
     *            Values for the IN clause.
     */
    public static Predicate in(final String expr, final List<?> values) {

        return new Predicate() {

            private String sql;

            public void init(AbstractSqlCreator creator) {

                StringBuilder sb = new StringBuilder();
                sb.append(expr).append(" in (");

                boolean first = true;
                for (Object value : values) {
                    String param = creator.allocateParameter();
                    creator.setParameter(param, value);
                    if (!first) {
                        sb.append(", ");
                    }
                    sb.append(":").append(param);
                    first = false;
                }

                sb.append(")");

                sql = sb.toString();

            }

            public String toSql() {
                return sql;
            }
        };
    }


    /**
     * Adds an IN clause to a creator.
     *
     * @param expr
     *            SQL expression to be tested for inclusion.
     * @param values
     *            Values for the IN clause.
     */
    public static Predicate in(final String expr, final Object... values) {
        return in(expr, Arrays.asList(values));
    }

    /**
     * Factory for 'and' and 'or' predicates.
     */
    private static Predicate join(final String joinWord, final List<Predicate> preds) {
        return new Predicate() {
            public void init(AbstractSqlCreator creator) {
                for (Predicate p : preds) {
                    p.init(creator);
                }
            }
            public String toSql() {
                StringBuilder sb = new StringBuilder()
                .append("(");
                boolean first = true;
                for (Predicate p : preds) {
                    if (!first) {
                        sb.append(" ").append(joinWord).append(" ");
                    }
                    sb.append(p.toSql());
                    first = false;
                }
                return sb.append(")").toString();
            }
        };
    }

    /**
     * Adds an EXISTS clause to a creator. Typical usage is as follows:
     *
     * <pre>
     * new SelectCreator()
     * .column("name")
     * .from("Emp e")
     * .where(exists("SickDay sd").where("sd.emp_id = e.id").and(eq("sd.dow", "Monday")));
     * </pre>
     *
     * @param table
     *            Table that forms the basis of the sub-select.
     */
    public static ExistsPredicate exists(String table) {
        return new ExistsPredicateImpl(table);
    }
    
    /**
     * Add a subQuery
     * <pre>
     * new SelectCreator()
     * .column("name")
     * .from("Emp e")
     * .where(inSubQuery("e.employee_id")
     * 			.column("s.supervisor_id"))
     * 			.from("supervisor s")
     * 			.where(in("s.department", values))
     * 		 );
     * </pre>
     * @param table
     * @return
     */
    public static SubQueryPredicate inSubQuery(String expr) {
    	return new SubQueryPredicatesImpl("in", expr);
    }

    /**
     * Adds a not equals clause to a creator.
     *
     * @param expr
     *            SQL expression to be compared for equality.
     * @param value
     *            Value to which the SQL expression is compared.
     */
    public static Predicate neq(final String expr, final Object value) {
        return new Predicate() {
            private String param;
            public void init(AbstractSqlCreator creator) {
                param = creator.allocateParameter();
                creator.setParameter(param, value);
            }
            public String toSql() {
                return String.format("%s <> :%s", expr, param);
            }
        };
    }
    
    public static Predicate is(final String expr, final java.sql.Date value) { 
        return new Predicate() {
            private String param;
            public void init(AbstractSqlCreator creator) {
                param = creator.allocateParameter();
                creator.setParameter(param, value);
            }
            public String toSql() {
                return String.format("%s >= :%s and %s < DATEADD(d,1,:%s)", expr, param, expr, param);
            }
        };
   }

    public static Predicate before(final String expr, final java.sql.Date value) { 
        return new Predicate() {
            private String param;
            public void init(AbstractSqlCreator creator) {
                param = creator.allocateParameter();
                creator.setParameter(param, value);
            }
            public String toSql() {
                return String.format("%s < :%s", expr, param);
            }
        };
   }
    
    public static Predicate after(final String expr, final java.sql.Date value) { 
        return new Predicate() {
            private String param;
            public void init(AbstractSqlCreator creator) {
                param = creator.allocateParameter();
                creator.setParameter(param, value);
            }
            public String toSql() {
                return String.format("%s > DATEADD(d,1,:%s)", expr, param);
            }
        };
   }

    /**
     * Inverts the sense of the given child predicate. In SQL terms, this
     * surrounds the given predicate with "not (...)".
     *
     * @param childPredicate
     *            Predicate whose sense is to be inverted.
     */
    public static Predicate not(final Predicate childPredicate) {
        return new Predicate() {
            public void init(AbstractSqlCreator creator) {
                childPredicate.init(creator);
            }
            public String toSql() {
                return "not (" + childPredicate.toSql() + ")";
            }
        };
    }

    /**
     * Joins a series of predicates with OR.
     */
    public static Predicate or(Predicate... predicates) {
        return join("or", Arrays.asList(predicates));
    }

    /**
     * Joins a series of predicates with OR.
     */
    public static Predicate or(List<Predicate> predicates) {
        return join("or", predicates);
    }


    /*
     * Ideas for other predicates:
     *
     * public static Predicate neq(final String expr, final Object value) { ... }
     *
     * public static Predicate notNull(String expr) { ... }
     *
     *
     * String:
     *
     * public static Predicate eqIgnoreCase(String expr, Object) { ... }
     * public static Predicate neqIgnoreCase(final String expr, final Object value) { ... }
     * public static Predicate contains(final String expr, final String value) { ... }
     * public static Predicate startsWith(final String expr, final String value) { ... }
     *
     *
     * Numeric:
     *
     * public static Predicate gt(String expr, Number) { ... }
     * public static Predicate gte(String expr, Number) { ... }
     * public static Predicate lt(String expr, Number) { ... }
     * public static Predicate lte(String expr, Number) { ... }
     *
     *
     * Date/time:
     *
     * public static Predicate after(String expr, java.util.Date) { ... }
     * public static Predicate before(String expr, java.util.Date) { ... }
     * public static Predicate between(String expr, java.sql.Date, java.sql.Date) { ... }
     * public static Predicate onOrAfter(String expr, java.sql.Date) { ... }
     * public static Predicate onOrBefore(String expr, java.sql.Date) { ... }
     *
     * public class Duration {
     *     public static Duration days(int days) { ... }
     *     public static Duration hours(int hours) { ... }
     *     public static Duration minutes(int minutes) { ... }
     * }
     *
     * public static Predicate olderThan(String expr, Duration duration) { ... }
     * public static Predicate within(String expr, Duration duration) { ... }
     *
     */
}
