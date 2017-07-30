package tdrz.gui.window.sub;

import org.eclipse.swt.events.SelectionEvent;

import tdrz.dto.memory.battle.AbstractBattle;
import tdrz.dto.memory.battle.BattleDto;
import tdrz.dto.translator.BattleDtoTranslator;
import tdrz.gui.window.WindowBase;
import tdrz.gui.window.main.ApplicationMain;
import tdrz.gui.window.sub.table.DropListTable;
import tool.FunctionUtils;

/**
 * 战斗流程<br>
 * {@link BattleWindow}中用,以及{@link DropListTable}中
 * @author MoeKagari
 */
public class BattleFlowWindow extends WindowBase {
	final BattleScrolledComposite sbc;//战斗流程窗口

	public BattleFlowWindow(ApplicationMain main) {
		super(main, "战斗流程");
		this.sbc = new BattleScrolledComposite(this.getCenterComposite(), 5);
	}

	@Override
	protected boolean defaultTopMost() {
		return true;
	}

	void updateBattle(BattleDto battleDto, SelectionEvent ev) {
		if (battleDto instanceof AbstractBattle) {
			this.sbc.clearWindow();
			if (ev != null) {
				this.updateBattle((AbstractBattle) battleDto);
				this.displayWindow();
			} else if (ev == null && this.getShell().isVisible()) {//自动更新(ev=null)时,需要此界面处于显示状态(最小化状态也可)
				this.updateBattle((AbstractBattle) battleDto);
			}
			this.sbc.layout(false);
		}
	}

	private void updateBattle(AbstractBattle battle) {
		this.updateWindowRedraw(FunctionUtils.getRunnable(BattleDtoTranslator::createBattleFlow, this.sbc.contentComposite, battle));
	}
}
