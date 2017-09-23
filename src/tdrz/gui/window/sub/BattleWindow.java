package tdrz.gui.window.sub;

import tdrz.core.config.AppConfig;
import tdrz.core.translator.BattleDtoTranslator;
import tdrz.gui.composite.BattleScrolledComposite;
import tdrz.gui.window.AbstractWindow;
import tdrz.update.context.GlobalContext;
import tdrz.update.context.data.DataType;
import tdrz.update.dto.memory.battle.BattleDto;
import tdrz.update.dto.memory.battle.info.InfoBattleShipdeckDto;

/**
 * 战斗窗口
 * @author MoeKagari
 */
public class BattleWindow extends AbstractWindow {
	private BattleDto lastInWindow = null;//最后一个battleDto(此面板中的)
	private final BattleScrolledComposite sbc;//战斗窗口
	private final BattleFlowWindow bfw;

	public BattleWindow(BattleFlowWindow bfw) {
		this.sbc = new BattleScrolledComposite(this.centerComposite, 0);
		this.bfw = bfw;
	}

	@Override
	public String defaultTitle() {
		return "出击";
	}

	@Override
	public void update(DataType type) {
		BattleDto last = GlobalContext.getMemorylist().getLastBattle();

		if (last != null) {
			if (AppConfig.get().isAutoUpdateBattleFlow()) {//自动更新
				this.bfw.updateBattle(last, null);
			}
			this.addNewBattle(last);
		} else {
			if (type == DataType.PORT) {
				this.clearWindow();
			}
		}
	}

	public void addNewBattle(BattleDto last) {
		//面板没有内容时,没有downarrow
		boolean haveDownArrow = this.sbc.contentComposite.getChildren().length != 0;
		//battleresult → shipdeck → next ,后两个之间加入downarrow
		haveDownArrow &= this.lastInWindow instanceof InfoBattleShipdeckDto;
		BattleDtoTranslator.newBattleComposite(this.sbc.contentComposite, this.bfw::updateBattle, haveDownArrow, last);

		this.lastInWindow = last;
		this.sbc.layoutContent(true);
	}

	public void clearWindow() {
		this.sbc.clearWindow();
		this.bfw.sbc.clearWindow();

		this.lastInWindow = null;
		this.sbc.layoutContent(true);
	}
}
