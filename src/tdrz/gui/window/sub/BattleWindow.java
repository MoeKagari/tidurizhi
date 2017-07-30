package tdrz.gui.window.sub;

import org.eclipse.swt.widgets.MenuItem;

import tdrz.config.AppConfig;
import tdrz.dto.memory.battle.BattleDto;
import tdrz.dto.memory.battle.info.InfoBattleShipdeckDto;
import tdrz.dto.translator.BattleDtoTranslator;
import tdrz.gui.window.WindowBase;
import tdrz.gui.window.main.ApplicationMain;
import tdrz.update.GlobalContext;
import tdrz.update.data.DataType;

/**
 * 战斗窗口
 * @author MoeKagari
 */
public class BattleWindow extends WindowBase {
	private BattleDto lastInWindow = null;//最后一个battleDto(此面板中的)
	private final BattleScrolledComposite sbc;//战斗窗口
	private final BattleFlowWindow bfw;

	public BattleWindow(ApplicationMain main, MenuItem menuItem, String title) {
		super(main, menuItem, title);
		this.sbc = new BattleScrolledComposite(this.getCenterComposite(), 0);
		this.bfw = new BattleFlowWindow(main);
	}

	@Override
	protected boolean defaultTopMost() {
		return true;
	}

	@Override
	public void update(DataType type) {
		BattleDto last = GlobalContext.getMemorylist().getLastBattle();
		if (last != null) {
			//自动更新
			if (AppConfig.get().isAutoUpdateBattleFlow()) {
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
		this.sbc.layout(true);
	}

	public BattleFlowWindow getBattleFlowWindow() {
		return this.bfw;
	}
}
