package tdrz.gui.window.sub;

import org.eclipse.swt.events.SelectionEvent;

import tdrz.core.translator.BattleDtoTranslator;
import tdrz.gui.window.main.ApplicationMain;
import tdrz.gui.window.sub.table.DropListTable;
import tdrz.gui.window.sup.WindowBase;
import tdrz.update.dto.memory.battle.AbstractBattle;
import tdrz.update.dto.memory.battle.BattleDto;
import tool.function.FunctionUtils;

/**
 * 战斗流程<br>
 * {@link BattleWindow}中用,以及{@link DropListTable}中
 * @author MoeKagari
 */
public class BattleFlowWindow extends WindowBase {
	public final BattleScrolledComposite sbc;//战斗流程窗口

	public BattleFlowWindow(ApplicationMain main) {
		super(main, "战斗流程");
		this.sbc = new BattleScrolledComposite(this.centerComposite, 5);
	}

	public void updateBattle(BattleDto battleDto, SelectionEvent ev) {
		if (battleDto instanceof AbstractBattle) {
			this.sbc.clearWindow();
			this.updateWindowRedraw(FunctionUtils.getRunnable(BattleDtoTranslator::createBattleFlow, this.sbc.contentComposite, (AbstractBattle) battleDto));
			FunctionUtils.notNull(ev, this::displayWindow);
			this.sbc.layoutContent(false);
		}
	}
}
