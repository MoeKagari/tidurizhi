package tdrz.gui.window.sub;

import tdrz.core.config.AppConfig;
import tdrz.core.translator.BattleDtoTranslator;
import tdrz.gui.composite.BattleScrolledComposite;
import tdrz.gui.window.sup.WindowBase;
import tdrz.update.context.GlobalContext;
import tdrz.update.context.data.DataType;
import tdrz.update.dto.memory.battle.BattleDto;
import tdrz.update.dto.memory.battle.info.InfoBattleShipdeckDto;

/**
 * 战斗窗口
 * @author MoeKagari
 */
public class BattleWindow extends WindowBase {
	private BattleDto lastInWindow = null;//最后一个battleDto(此面板中的)
	private final BattleScrolledComposite sbc;//战斗窗口
	private final BattleFlowWindow bfw;

	public BattleWindow() {
		this.sbc = new BattleScrolledComposite(this.centerComposite, 0);
		this.bfw = new BattleFlowWindow();
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

			//面板没有内容时,没有downarrow
			boolean haveDownArrow = this.sbc.contentComposite.getChildren().length != 0;
			//battleresult → shipdeck → next ,后两个之间加入downarrow
			haveDownArrow &= this.lastInWindow instanceof InfoBattleShipdeckDto;
			BattleDtoTranslator.newBattleComposite(this.sbc.contentComposite, this.bfw::updateBattle, haveDownArrow, last);
		} else {
			if (type == DataType.PORT) {
				this.sbc.clearWindow();
				this.bfw.sbc.clearWindow();
			}
		}

		this.lastInWindow = last;
		this.sbc.layoutContent(true);
	}

	public BattleFlowWindow getBattleFlowWindow() {
		return this.bfw;
	}
}
