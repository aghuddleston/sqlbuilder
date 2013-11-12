package ca.krasnay.sqlbuilder;

import java.util.ArrayList;
import java.util.List;

public class SubQueryPredicatesImpl implements SubQueryPredicate {
	
	private List<Predicate> predicates = new ArrayList<Predicate>();
	
	private SelectBuilder selectBuilder;
	private String operator;
	private String expr;
	
	public SubQueryPredicatesImpl(String operator, String expr) {
		this.selectBuilder = new SelectBuilder();
		this.operator = operator;
		this.expr = expr;
	}

	@Override
	public void init(AbstractSqlCreator creator) {
		for (Predicate predicate : predicates) {
			predicate.init(creator);
			selectBuilder.where(predicate.toSql());
		}
	}

	@Override
	public SubQueryPredicate column(String name) {
		this.selectBuilder.column(name);
		return this;
	}
	
	@Override
	public SubQueryPredicate from(String table) {
		this.selectBuilder.from(table);
		return this;
	}

	@Override
	public String toSql() {
		return expr + " " + operator + " ("+ selectBuilder + ")";
	}

	@Override
	public SubQueryPredicate and(Predicate predicate) {
		return where(predicate);
	}

	@Override
	public SubQueryPredicate and(String predicate) {
		return where(predicate);
	}

	@Override
	public SubQueryPredicate where(Predicate predicate) {
		predicates.add(predicate);
		return this;
	}

	@Override
	public SubQueryPredicate where(String predicate) {
		selectBuilder.where(predicate);
		return this;
	}

}
