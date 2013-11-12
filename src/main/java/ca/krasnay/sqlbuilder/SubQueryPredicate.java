package ca.krasnay.sqlbuilder;

/**
 * Predicate used to add a sub-query to where a clause.
 *
 * @author Annie Huddleston
 */
public interface SubQueryPredicate extends Predicate {
	
	public SubQueryPredicate column(String name);
	
	public SubQueryPredicate from(String table);
	
	public SubQueryPredicate and(Predicate predicate);

	public SubQueryPredicate and(String predicate);

	public SubQueryPredicate where(Predicate predicate);

	public SubQueryPredicate where(String predicate);
}
