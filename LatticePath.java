// SPDX-FileCopyrightText: 2023 Carlos Rica ( jasampler AT gmail DOT com )
// SPDX-License-Identifier: GPL-3.0-or-later
/**
 * LatticePath - Generates all the different paths from the 0,0 corner to the
 * opposite corner in a grid of points, optionally surpassing the diagonal and
 * optionally including diagonal steps. Also, a table with the number of
 * all the different paths to each point of the grid can be generated too.
 */
import java.util.function.Consumer;
public class LatticePath {

	public static final byte EMPTY = 0;
	public static final byte HORIZ = 1;
	public static final byte VERT = 2;
	public static final byte DIAG = 3;

	public final int rows;
	public final int cols;
	public final boolean passDiag;
	public final boolean diagStep;

	private byte[] steps;
	private int nsteps;
	private int row;
	private int col;

	/** Creates a generator of paths from the 0,0 corner to the opposite
	 * in a grid with the given rows and columns, optionally surpassing the
	 * diagonal and optionally including diagonal steps or not. */
	public LatticePath(int rows, int cols,
			boolean passDiag, boolean diagStep) {
		this.rows = rows;
		this.cols = cols;
		this.passDiag = passDiag;
		this.diagStep = diagStep;
	}

	/** Returns a step of the current generated path. */
	public byte get(int i) {
		return steps[i];
	}

	/** Returns the number of steps saved in the current generated path. */
	public int length() {
		return nsteps;
	}

	/** Returns a table with the number of paths to each point. */
	public long[][] count() {
		long[][] t = new long[rows][];
		for (int i = 0; i < rows; i++) {
			t[i] = new long[cols];
		}
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				long n;
				if (! passDiag && i > j) {
					n = 0;
				} else if (i != 0 && j != 0) {
					n = sumLongs(t[i][j - 1], t[i - 1][j]);
					if (diagStep) {
						n = sumLongs(n,t[i - 1][j - 1]);
					}
				} else {
					n = 1;
				}
				t[i][j] = n;
			}
		}
		return t;
	}

	// Sums two longs and returns -1 when overflows or finds a negative.
	private static long sumLongs(long n1, long n2) {
		return n1 < 0 || n2 < 0 || n1 + n2 < 0 ? -1 : n1 + n2;
	}

	/** Generates all paths calling to the given callback on each one. */
	public void generate(Consumer<LatticePath> action) {
		steps = new byte[(rows - 1) + (cols - 1)];
		row = col = nsteps = 0;
		addSteps(action);
	}

	// Saves in the byte array the next steps from the current position
	// or calls to the given action if the end point was already reached.
	private void addSteps(Consumer<LatticePath> action) {
		if (row == rows - 1 && col == cols - 1) {
			action.accept(this);
		} else {
			if (col < cols - 1) {
				steps[nsteps++] = HORIZ;
				col++;
				addSteps(action);
				col--;
				steps[--nsteps] = EMPTY;
			}
			if (diagStep && row < rows - 1 && col < cols - 1) {
				steps[nsteps++] = DIAG;
				row++;
				col++;
				addSteps(action);
				row--;
				col--;
				steps[--nsteps] = EMPTY;
			}
			if (row < rows - 1 && (passDiag || row < col)) {
				steps[nsteps++] = VERT;
				row++;
				addSteps(action);
				row--;
				steps[--nsteps] = EMPTY;
			}
		}
	}

	private static final String NL = System.lineSeparator();

	private static StringBuilder sb; //temporary buffer to print the paths

	public static void main(String[] args) {
		boolean optPass = false;
		boolean optDiag = false;
		boolean optCount = false;
		int numArgs = 0;
		int rows = 0;
		int cols = 0;
		for (int i = 0; i < args.length; i++) {
			String arg = args[i];
			if (arg.equals("--pass")) {
				optPass = true;
			} else if (arg.equals("--diag")) {
				optDiag = true;
			} else if (arg.equals("--count")) {
				optCount = true;
			} else if (arg.matches("\\d+")) {
				numArgs++;
				if (numArgs == 1) {
					rows = Integer.parseInt(arg);
				} else if (numArgs == 2) {
					cols = Integer.parseInt(arg);
				}
			}
		}
		if (numArgs == 1) {
			cols = rows;
		}
		if (rows < 1 || cols < 1) {
			System.err.println("Error: Parameters: java LatticePath"
					+ " [--pass] [--diag] [--count]"
					+ " ROWS [COLUMNS]");
			return;
		}
		LatticePath path = new LatticePath(rows,cols,optPass,optDiag);
		if (optCount) {
			print(path.count());
		} else {
			sb = new StringBuilder();
			path.generate(LatticePath::print);
		}
	}

	// Prints the matrix with the number of paths to each point.
	private static void print(long[][] t) {
		StringBuilder sb = new StringBuilder();
		int rows = t.length;
		for (int i = 0; i < rows; i++) {
			int cols = t[i].length;
			for (int j = 0; j < cols; j++) {
				if (j != 0) {
					sb.append('\t');
				}
				long n = t[i][j];
				if (n < 0) {
					sb.append('?');
				} else {
					sb.append(n);
				}
			}
			sb.append(NL);
		}
		System.out.print(sb);
	}

	// Prints in ASCII the steps of the given path.
	private static void print(LatticePath path) {
		sb.setLength(0);
		int indent = 0;
		for (int i = 0; i < path.length(); i++) {
			indent = appendChars(sb, "o", indent);
			byte step = path.get(i);
			if (step == HORIZ) {
				indent = appendChars(sb, "---", indent);
			} else if (step == VERT) {
				appendNL(sb, --indent);
				indent = appendChars(sb, "|", indent);
				appendNL(sb, --indent);
			} else if (step == DIAG) {
				appendNL(sb, indent);
				indent = appendChars(sb, "'.", indent);
				appendNL(sb, indent);
				indent = appendChars(sb, "'", indent);
			}
		}
		appendChars(sb, "o", 0);
		appendNL(sb, 0);
		System.out.println(sb);
	}

	private static int appendChars(StringBuilder sb, String chars,
			int indent) {
		sb.append(chars);
		return indent + chars.length();
	}

	private static void appendNL(StringBuilder sb, int indent) {
		sb.append(NL);
		for (int i = 0; i < indent; i++) {
			sb.append(' ');
		}
	}

}

