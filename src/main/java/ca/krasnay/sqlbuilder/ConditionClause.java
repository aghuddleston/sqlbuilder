package ca.krasnay.sqlbuilder;

public class ConditionClause {

	public ConditionClause(String condition, String expr) {
		super();
		this.condition = condition;
		this.expr = expr;
	}

	private String condition;
	private String expr;

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public String getExpr() {
		return expr;
	}

	public void setExpr(String expr) {
		this.expr = expr;
	}

	@Override
	public String toString() {
		return condition + " " + expr;
	}

}
