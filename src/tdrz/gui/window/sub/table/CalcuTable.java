package tdrz.gui.window.sub.table;

import java.util.HashMap;
import java.util.Map;

import tdrz.gui.window.main.ApplicationMain;
import tdrz.gui.window.sub.AbstractTable;
import tool.FunctionUtils;

public abstract class CalcuTable<T> extends AbstractTable<T> {
	public CalcuTable(ApplicationMain main, String title) {
		super(main, title);
	}

	@Override
	protected boolean disposeAndUpdate() {
		return false;
	}

	@Override
	protected boolean isTableSortable() {
		return false;
	}

	@Override
	protected boolean haveRowHeader() {
		return false;
	}

	protected enum Eval {
		S("S胜利", 1.2),
		AB("AB胜利", 1.0),
		C("C败北", 0.8),
		D("D败北", 0.7);

		protected final static Map<String, Eval> EVALMAP = new HashMap<>();
		static {
			FunctionUtils.forEach(Eval.values(), eval -> EVALMAP.put(eval.name, eval));
		}

		protected final String name;
		protected final double value;

		private Eval(String name, double value) {
			this.name = name;
			this.value = value;
		}
	}

	protected static class ShipExp {
		protected static final Map<Integer, Integer> EXPMAP = new HashMap<>();
		static {
			EXPMAP.put(1, 0);
			EXPMAP.put(2, 100);
			EXPMAP.put(3, 300);
			EXPMAP.put(4, 600);
			EXPMAP.put(5, 1000);
			EXPMAP.put(6, 1500);
			EXPMAP.put(7, 2100);
			EXPMAP.put(8, 2800);
			EXPMAP.put(9, 3600);
			EXPMAP.put(10, 4500);
			EXPMAP.put(11, 5500);
			EXPMAP.put(12, 6600);
			EXPMAP.put(13, 7800);
			EXPMAP.put(14, 9100);
			EXPMAP.put(15, 10500);
			EXPMAP.put(16, 12000);
			EXPMAP.put(17, 13600);
			EXPMAP.put(18, 15300);
			EXPMAP.put(19, 17100);
			EXPMAP.put(20, 19000);
			EXPMAP.put(21, 21000);
			EXPMAP.put(22, 23100);
			EXPMAP.put(23, 25300);
			EXPMAP.put(24, 27600);
			EXPMAP.put(25, 30000);
			EXPMAP.put(26, 32500);
			EXPMAP.put(27, 35100);
			EXPMAP.put(28, 37800);
			EXPMAP.put(29, 40600);
			EXPMAP.put(30, 43500);
			EXPMAP.put(31, 46500);
			EXPMAP.put(32, 49600);
			EXPMAP.put(33, 52800);
			EXPMAP.put(34, 56100);
			EXPMAP.put(35, 59500);
			EXPMAP.put(36, 63000);
			EXPMAP.put(37, 66600);
			EXPMAP.put(38, 70300);
			EXPMAP.put(39, 74100);
			EXPMAP.put(40, 78000);
			EXPMAP.put(41, 82000);
			EXPMAP.put(42, 86100);
			EXPMAP.put(43, 90300);
			EXPMAP.put(44, 94600);
			EXPMAP.put(45, 99000);
			EXPMAP.put(46, 103500);
			EXPMAP.put(47, 108100);
			EXPMAP.put(48, 112800);
			EXPMAP.put(49, 117600);
			EXPMAP.put(50, 122500);
			EXPMAP.put(51, 127500);
			EXPMAP.put(52, 132700);
			EXPMAP.put(53, 138100);
			EXPMAP.put(54, 143700);
			EXPMAP.put(55, 149500);
			EXPMAP.put(56, 155500);
			EXPMAP.put(57, 161700);
			EXPMAP.put(58, 168100);
			EXPMAP.put(59, 174700);
			EXPMAP.put(60, 181500);
			EXPMAP.put(61, 188500);
			EXPMAP.put(62, 195800);
			EXPMAP.put(63, 203400);
			EXPMAP.put(64, 211300);
			EXPMAP.put(65, 219500);
			EXPMAP.put(66, 228000);
			EXPMAP.put(67, 236800);
			EXPMAP.put(68, 245900);
			EXPMAP.put(69, 255300);
			EXPMAP.put(70, 265000);
			EXPMAP.put(71, 275000);
			EXPMAP.put(72, 285400);
			EXPMAP.put(73, 296200);
			EXPMAP.put(74, 307400);
			EXPMAP.put(75, 319000);
			EXPMAP.put(76, 331000);
			EXPMAP.put(77, 343400);
			EXPMAP.put(78, 356200);
			EXPMAP.put(79, 369400);
			EXPMAP.put(80, 383000);
			EXPMAP.put(81, 397000);
			EXPMAP.put(82, 411500);
			EXPMAP.put(83, 426500);
			EXPMAP.put(84, 442000);
			EXPMAP.put(85, 458000);
			EXPMAP.put(86, 474500);
			EXPMAP.put(87, 491500);
			EXPMAP.put(88, 509000);
			EXPMAP.put(89, 527000);
			EXPMAP.put(90, 545500);
			EXPMAP.put(91, 564500);
			EXPMAP.put(92, 584500);
			EXPMAP.put(93, 606500);
			EXPMAP.put(94, 631500);
			EXPMAP.put(95, 661500);
			EXPMAP.put(96, 701500);
			EXPMAP.put(97, 761500);
			EXPMAP.put(98, 851500);
			EXPMAP.put(99, 1000000);
			EXPMAP.put(100, 1000000);
			EXPMAP.put(101, 1010000);
			EXPMAP.put(102, 1011000);
			EXPMAP.put(103, 1013000);
			EXPMAP.put(104, 1016000);
			EXPMAP.put(105, 1020000);
			EXPMAP.put(106, 1025000);
			EXPMAP.put(107, 1031000);
			EXPMAP.put(108, 1038000);
			EXPMAP.put(109, 1046000);
			EXPMAP.put(110, 1055000);
			EXPMAP.put(111, 1065000);
			EXPMAP.put(112, 1077000);
			EXPMAP.put(113, 1091000);
			EXPMAP.put(114, 1107000);
			EXPMAP.put(115, 1125000);
			EXPMAP.put(116, 1145000);
			EXPMAP.put(117, 1168000);
			EXPMAP.put(118, 1194000);
			EXPMAP.put(119, 1223000);
			EXPMAP.put(120, 1255000);
			EXPMAP.put(121, 1290000);
			EXPMAP.put(122, 1329000);
			EXPMAP.put(123, 1372000);
			EXPMAP.put(124, 1419000);
			EXPMAP.put(125, 1470000);
			EXPMAP.put(126, 1525000);
			EXPMAP.put(127, 1584000);
			EXPMAP.put(128, 1647000);
			EXPMAP.put(129, 1714000);
			EXPMAP.put(130, 1785000);
			EXPMAP.put(131, 1860000);
			EXPMAP.put(132, 1940000);
			EXPMAP.put(133, 2025000);
			EXPMAP.put(134, 2115000);
			EXPMAP.put(135, 2210000);
			EXPMAP.put(136, 2310000);
			EXPMAP.put(137, 2415000);
			EXPMAP.put(138, 2525000);
			EXPMAP.put(139, 2640000);
			EXPMAP.put(140, 2760000);
			EXPMAP.put(141, 2887000);
			EXPMAP.put(142, 3021000);
			EXPMAP.put(143, 3162000);
			EXPMAP.put(144, 3310000);
			EXPMAP.put(145, 3465000);
			EXPMAP.put(146, 3628000);
			EXPMAP.put(147, 3799000);
			EXPMAP.put(148, 3978000);
			EXPMAP.put(149, 4165000);
			EXPMAP.put(150, 4360000);
			EXPMAP.put(151, 4564000);
			EXPMAP.put(152, 4777000);
			EXPMAP.put(153, 4999000);
			EXPMAP.put(154, 5230000);
			EXPMAP.put(155, 5470000);
			EXPMAP.put(156, 5720000);
			EXPMAP.put(157, 5780000);
			EXPMAP.put(158, 5860000);
			EXPMAP.put(159, 5970000);
			EXPMAP.put(160, 6120000);
			EXPMAP.put(161, 6320000);
			EXPMAP.put(162, 6580000);
			EXPMAP.put(163, 6910000);
			EXPMAP.put(164, 7320000);
			EXPMAP.put(165, 7820000);
		}
	}
}
