package ca.krasnay.sqlbuilder;

import java.io.Serializable;

public class SqlServer2008Dialect implements Dialect, Serializable {

    private static final long serialVersionUID = 1;

	@Override
	public String createCountSelect(String sql) {
        return "select count(*) from (" + sql + ") a";
	}

	/**
	 * Assumes have a column("row_number() over (order by " + sortByParam + ") as RowNumber")
	 * in the query
	 */
	@Override
	public String createPageSelect(String sql, int startRow, int endRow) {
        return String.format("select * from (%s) a " +
        		"where RowNumber between %s and %s", sql, startRow, endRow);
	}

}
