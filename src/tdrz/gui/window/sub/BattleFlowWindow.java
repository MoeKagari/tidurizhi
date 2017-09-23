package tdrz.gui.window.sub;

import org.eclipse.swt.events.SelectionEvent;

import tdrz.core.translator.BattleDtoTranslator;
import tdrz.gui.composite.BattleScrolledComposite;
import tdrz.gui.window.AbstractWindowBase;
import tdrz.gui.window.sub.table.DropListTable;
import tdrz.update.dto.memory.battle.AbstractBattle;
import tdrz.update.dto.memory.battle.BattleDto;
import tool.function.FunctionUtils;

/**
 * 战斗流程<br>
 * {@link BattleWindow}中用,以及{@link DropListTable}中
 * @author MoeKagari
 */
public class BattleFlowWindow extends AbstractWindowBase {
	public final BattleScrolledComposite sbc;//战斗流程窗口

	public BattleFlowWindow() {
		this.sbc = new BattleScrolledComposite(this.centerComposite, 5);
	}

	@Override
	protected boolean canMaxSize() {
		return true;
	}

	@Override
	public String defaultTitle() {
		return "战斗流程";
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
