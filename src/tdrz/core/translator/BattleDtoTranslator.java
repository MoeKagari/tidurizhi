package tdrz.core.translator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.DoubleBinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import tdrz.core.config.AppConstants;
import tdrz.core.internal.TrayMessageBox;
import tdrz.core.logic.HPMessage;
import tdrz.core.util.SwtUtils;
import tdrz.gui.window.main.ApplicationMain;
import tdrz.update.context.GlobalContext;
import tdrz.update.dto.memory.battle.AbstractBattle;
import tdrz.update.dto.memory.battle.AbstractBattle.BattleDeck;
import tdrz.update.dto.memory.battle.AbstractBattle.BattleDeckAttackDamage;
import tdrz.update.dto.memory.battle.AbstractBattle.BattleOneAttack;
import tdrz.update.dto.memory.battle.AbstractBattleDay;
import tdrz.update.dto.memory.battle.AbstractBattleDay.BattleDayStage;
import tdrz.update.dto.memory.battle.AbstractBattleDay.InjectionKouko;
import tdrz.update.dto.memory.battle.AbstractBattleDay.Kouko;
import tdrz.update.dto.memory.battle.AbstractBattleDay.OpeningAttack;
import tdrz.update.dto.memory.battle.AbstractBattleDay.OpeningTaisen;
import tdrz.update.dto.memory.battle.AbstractBattleMidnight;
import tdrz.update.dto.memory.battle.AbstractInfoBattle;
import tdrz.update.dto.memory.battle.AbstractInfoBattleResult;
import tdrz.update.dto.memory.battle.AbstractInfoBattleResult.GetShip;
import tdrz.update.dto.memory.battle.AbstractInfoBattleStartNext;
import tdrz.update.dto.memory.battle.AbstractInfoBattleStartNext.DestructionBattle;
import tdrz.update.dto.memory.battle.BattleDto;
import tdrz.update.dto.memory.battle.info.InfoBattleGobackPortDto;
import tdrz.update.dto.memory.battle.info.InfoBattleShipdeckDto;
import tdrz.update.dto.memory.battle.info.InfoBattleStartAirBaseDto;
import tdrz.update.dto.memory.battle.info.InfoBattleStartDto;
import tdrz.update.dto.memory.battle.practice.PracticeBattleDayDto;
import tdrz.update.dto.memory.battle.practice.PracticeBattleResultDto;
import tdrz.update.dto.word.DeckDto;
import tool.function.FunctionUtils;
import tool.function.funcinte.BiIntFunction;

public class BattleDtoTranslator {

	public static void newBattleComposite(Composite parent, BiConsumer<AbstractBattle, SelectionEvent> handler, boolean hasDownArrow, BattleDto lastOne) {
		Runnable insertDownArrow = () -> {
			Composite composite = new Composite(parent, SWT.CENTER);
			composite.setLayout(SwtUtils.makeGridLayout(1, 0, 0, 0, 0, 4, 4));
			composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			SwtUtils.initLabel(new Label(composite, SWT.CENTER), "↓", new GridData(GridData.FILL_HORIZONTAL));
		};

		if (lastOne instanceof InfoBattleStartDto) {
			BTResult btr = newBattleStart((InfoBattleStartDto) lastOne);
			if (btr != null) {
				newOneBattleComposite(parent, btr, null, null);
				insertDownArrow.run();
			}
		}

		BTResult btr = BattleDtoTranslator.getBattle(lastOne);
		if (btr != null) {
			if (lastOne instanceof PracticeBattleDayDto) {//演习开始
				SwtUtils.initLabel(new Label(parent, SWT.CENTER), "演习", new GridData(GridData.FILL_HORIZONTAL));
				insertDownArrow.run();
			} else if ((lastOne instanceof PracticeBattleResultDto) | hasDownArrow) {//演习结束也加下箭头
				insertDownArrow.run();
			}
			newOneBattleComposite(parent, btr, handler, lastOne);
		}
	}

	private static void newOneBattleComposite(Composite parent, BTResult btr, BiConsumer<AbstractBattle, SelectionEvent> handler, BattleDto lastOne) {
		Composite base = new Composite(parent, SWT.CENTER);
		base.setLayout(SwtUtils.makeGridLayout(1, 0, 0, 0, 0));
		base.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		//行进信息
		FunctionUtils.notNull(btr.deckInformations, di -> newLabels(base, di));
		//战斗前后状态
		if (btr.before != null && btr.after != null) {
			Composite stateComposite = new Composite(base, SWT.NONE);
			stateComposite.setLayout(SwtUtils.makeGridLayout(3, 4, 0, 0, 0, 1, 1));
			stateComposite.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));

			newStateComposite(stateComposite, btr.before);
			{
				Composite centerComposite = new Composite(stateComposite, SWT.NONE);
				centerComposite.setLayout(SwtUtils.makeGridLayout(1, 0, 0, 0, 0));
				centerComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

				if (lastOne instanceof AbstractBattle) {
					if (haveDamage((AbstractBattle) lastOne) == false) {
						SwtUtils.initLabel(new Label(centerComposite, SWT.CENTER), "无伤", new GridData(SWT.CENTER, SWT.CENTER, true, true));
					}
				}
				SwtUtils.initLabel(new Label(centerComposite, SWT.CENTER), "→", new GridData(SWT.CENTER, SWT.CENTER, true, true));
			}
			newStateComposite(stateComposite, btr.after);

			if (lastOne instanceof AbstractBattle) {//右键菜单,只对battle有效
				MenuItem show = new MenuItem(new Menu(stateComposite), SWT.PUSH);
				show.setText("战斗流程");
				if (handler != null) {
					show.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(SelectionEvent ev) {
							handler.accept((AbstractBattle) lastOne, ev);
						}
					});
				}
				SwtUtils.setMenuRecursively(stateComposite, show.getParent());
			}
		}
	}

	private static void newLabels(Composite composite, ArrayList<String> deckInformation) {
		deckInformation.stream().map(text -> SwtUtils.setText(new Label(composite, SWT.CENTER), text)).forEach(label -> label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL)));
	}

	private static void newStateComposite(Composite composite, ArrayList<String[]> shipInformations) {
		int length = shipInformations.stream().filter(FunctionUtils::isNotNull).mapToInt(strs -> strs.length).max().orElse(0);

		BiIntFunction<String> getText = (i, j) -> {
			if (j >= shipInformations.size() || j < 0) return "";
			String[] strs = shipInformations.get(j);
			if (i >= strs.length || i < 0) return "";
			return strs[i];
		};

		Composite oneSide = new Composite(composite, SWT.BORDER);
		oneSide.setLayout(SwtUtils.makeGridLayout(length, 4, 0, 0, 0));
		oneSide.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));
		for (int i = 0; i < length; i++) {
			int nullIndex = 0;
			Composite oneState = new Composite(oneSide, SWT.NONE);
			oneState.setLayout(SwtUtils.makeGridLayout(1, 0, 4, 0, 0));
			oneState.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));
			{
				Composite oneUpState = new Composite(oneState, SWT.NONE);
				oneUpState.setLayout(SwtUtils.makeGridLayout(1, 0, 0, 0, 0));
				oneUpState.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));
				for (int j = 0; j < shipInformations.size(); j++) {
					if (shipInformations.get(j) == null) {
						nullIndex = j;
						break;
					}
					String text = getText.apply(i, j);
					Color background = HPMessage.getColor(text);
					SwtUtils.initLabel(new Label(oneUpState, SWT.CENTER), text, new GridData(SWT.CENTER, SWT.CENTER, true, true), background);
				}
			}
			{
				Composite oneDownState = new Composite(oneState, SWT.NONE);
				oneDownState.setLayout(SwtUtils.makeGridLayout(1, 0, 0, 0, 0));
				oneDownState.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));
				for (int j = nullIndex + 1; j < shipInformations.size(); j++) {
					String text = getText.apply(i, j);
					Color background = HPMessage.getColor(text);
					SwtUtils.initLabel(new Label(oneDownState, SWT.CENTER), text, new GridData(SWT.CENTER, SWT.CENTER, true, true), background);
				}
			}
		}
	}

	private static BTResult getBattle(BattleDto battleDto) {
		if (battleDto instanceof AbstractInfoBattle) {
			if (battleDto instanceof AbstractInfoBattleStartNext) {
				return newBattleStartNext((AbstractInfoBattleStartNext) battleDto);
			}
			if (battleDto instanceof InfoBattleStartAirBaseDto) {
				return newBattleStartAirBase((InfoBattleStartAirBaseDto) battleDto);
			}
			if (battleDto instanceof AbstractInfoBattleResult) {
				return newBattleResult((AbstractInfoBattleResult) battleDto);
			}
			if (battleDto instanceof InfoBattleGobackPortDto) {
				return newBattleGobackPort((InfoBattleGobackPortDto) battleDto);
			}
			if (battleDto instanceof InfoBattleShipdeckDto) {
				return newBattleShipdeck((InfoBattleShipdeckDto) battleDto);
			}
		}
		if (battleDto instanceof AbstractBattle) {
			return newBattleDayMidnight((AbstractBattle) battleDto);
		}
		return null;
	}

	private static BTResult newBattleStart(InfoBattleStartDto battleStart) {
		ArrayList<String> deckInformations = new ArrayList<>();

		int deckId = battleStart.getDeckId();
		String deckString;
		if (battleStart.isCombined() && deckId == 1) {
			deckString = "联合舰队";
		} else {
			deckString = AppConstants.DEFAULT_FLEET_NAME[deckId - 1];
		}
		deckInformations.add(deckString + " → " + battleStart.getMapString() + "-" + battleStart.getStart());

		//大破检查
		DeckDto[] decks;//出击的deck
		TrayMessageBox box = new TrayMessageBox();
		if (battleStart.isCombined() && deckId == 1) {
			decks = new DeckDto[] { GlobalContext.deckRooms[0].getDeck(), GlobalContext.deckRooms[1].getDeck() };
		} else {
			decks = new DeckDto[] { GlobalContext.deckRooms[deckId - 1].getDeck() };
		}
		if (Arrays.stream(decks).anyMatch(FunctionUtils::isNull)) {
			String message = "出击舰队状态未知,请注意";
			deckInformations.add(message);
			box.add("警告", message);
		} else {
			if (Arrays.stream(decks).anyMatch(DeckDtoTranslator::hasDapo)) {
				String message = "出击舰队中有大破舰娘,请注意";
				deckInformations.add(message);
				box.add("大破", message);
			}
		}
		TrayMessageBox.show(ApplicationMain.main, box);

		return new BTResult(deckInformations, null, null);
	}

	private static BTResult newBattleStartNext(AbstractInfoBattleStartNext battleNext) {
		ArrayList<String> deckInformations = new ArrayList<>();

		String text = "地图:" + battleNext.getMapString() + ",Cell:" + battleNext.getNext() + "(" +//
		//下一点的类型
				battleNext.getNextType() +
				//获得资源
				FunctionUtils.notNull(battleNext.getItems(), items -> StringUtils.join(items, ','), "") +
				//终点?
				(battleNext.isGoal() ? ",终点" : "") + ")";
		deckInformations.add(text);

		//涡潮
		String[] happening = battleNext.getHappening();
		if (happening != null) {
			text = String.format("涡潮种类:%s 电探:%s 掉落量:%s", happening[0], happening[1], happening[2]);
			deckInformations.add(text);
		}

		//基地受损
		DestructionBattle destructionBattle = battleNext.getDestructionBattle();
		if (destructionBattle != null) {
			{
				int len = destructionBattle.getBaseNumber();
				text = "基地受损:" + Arrays.toString(Arrays.copyOfRange(destructionBattle.getBefore(), 0, 0 + len)) + "→" + Arrays.toString(Arrays.copyOfRange(destructionBattle.getAfter(), 0, 0 + len));
				deckInformations.add(text);
			}
			FunctionUtils.notNull(destructionBattle.getSeiku(), deckInformations::add);
			FunctionUtils.forEach(destructionBattle.getLostKind().split("\n"), deckInformations::add);
		}

		//侦察(侦察点only)
		String[] airsearch = battleNext.getAirsearch();
		if (airsearch != null) {
			text = String.format("侦察机:%s 侦查结果:%s", airsearch[0], airsearch[1]);
			deckInformations.add(text);
		}

		return new BTResult(deckInformations, null, null);
	}

	private static BTResult newBattleResult(AbstractInfoBattleResult battleResult) {
		ArrayList<String> deckInformations = new ArrayList<>();

		String text = "战斗结果:" + battleResult.getRank() + " " + "MVP:" + battleResult.getMvp() + (battleResult.getMvpCombined() > 0 ? ("," + battleResult.getMvpCombined()) : "");
		deckInformations.add(text);

		GetShip newShip = battleResult.getNewShip();
		if (newShip != null) {
			deckInformations.add(newShip.getType() + "-" + newShip.getName() + " 加入镇守府");
		}

		return new BTResult(deckInformations, null, null);
	}

	private static BTResult newBattleShipdeck(InfoBattleShipdeckDto battleShipdeck) {
		if (battleShipdeck.hasDapo()) {
			ArrayList<String> deckInformations = new ArrayList<>();
			deckInformations.add("出击舰队中有大破舰娘,请注意");

			TrayMessageBox box = new TrayMessageBox("大破", StringUtils.join(deckInformations, "\n"));
			TrayMessageBox.show(ApplicationMain.main, box);

			return new BTResult(deckInformations, null, null);
		}
		return null;
	}

	private static BTResult newBattleDayMidnight(AbstractBattle battleDto) {
		ArrayList<String> deckInformations = new ArrayList<>();
		ArrayList<String[]> before = new ArrayList<>();
		ArrayList<String[]> after = new ArrayList<>();

		BiConsumer<BattleDeck, BattleDeckAttackDamage> addOneState = (bd, bdad) -> {
			if (AbstractBattle.existBattleDeck(bd) && bdad != null) {
				int length = bd.getDeckLength();
				int[] nowhps = bd.nowhps;
				int[] maxhps = bd.maxhps;
				int[] dmgs = bdad.dmgs;

				String[] oneBefore = new String[length];
				String[] oneAfter = new String[length];
				for (int index = 0; index < length; index++) {
					oneBefore[index] = bd.escapes.contains(index) ? HPMessage.ESCAPE_STRING : HPMessage.getString(FunctionUtils.division(nowhps[index], maxhps[index]));
					oneAfter[index] = bd.escapes.contains(index) ? HPMessage.ESCAPE_STRING : HPMessage.getString(FunctionUtils.division(nowhps[index] - dmgs[index], maxhps[index]));
				}

				before.add(oneBefore);
				after.add(oneAfter);
			}
		};

		addOneState.accept(battleDto.getfDeck(), battleDto.getfDeckAttackDamage());
		addOneState.accept(battleDto.getfDeckCombine(), battleDto.getfDeckCombineAttackDamage());

		before.add(null);
		after.add(null);

		addOneState.accept(battleDto.geteDeck(), battleDto.geteDeckAttackDamage());
		addOneState.accept(battleDto.geteDeckCombine(), battleDto.geteDeckCombineAttackDamage());

		if (battleDto instanceof AbstractBattleMidnight) {
			AbstractBattleMidnight battleMidnight = (AbstractBattleMidnight) battleDto;
			if (battleMidnight.isMidnightOnly() == false) {
				deckInformations.add("夜战");
			}
		}

		return new BTResult(deckInformations, before, after);
	}

	private static BTResult newBattleGobackPort(InfoBattleGobackPortDto battleGobackPort) {
		ArrayList<String> deckInformations = new ArrayList<>();
		deckInformations.add("大破的舰娘已退避");
		return new BTResult(deckInformations, null, null);
	}

	private static BTResult newBattleStartAirBase(InfoBattleStartAirBaseDto battleStartAirBase) {
		ArrayList<String> deckInformations = new ArrayList<>();

		String[] NUMBERS = { "一", "二", "三", "四", "五", "六" };
		int[][] strikePoints = battleStartAirBase.getStrikePoint();
		for (int i = 0; i < strikePoints.length; i++) {
			String number = NUMBERS[i];
			int[] strikePoint = strikePoints[i];
			FunctionUtils.notNull(strikePoint, sp -> deckInformations.add("第" + number + "基地航空队 -> " + Arrays.toString(sp)));
		}

		return new BTResult(deckInformations, null, null);
	}

	private static class BTResult {
		final ArrayList<String> deckInformations;
		final ArrayList<String[]> before;
		final ArrayList<String[]> after;

		public BTResult(ArrayList<String> deckInformations, ArrayList<String[]> before, ArrayList<String[]> after) {
			this.deckInformations = deckInformations;
			this.before = before;
			this.after = after;
		}
	}

	/*------------------------------------------------------------------------------*/

	public static void createBattleFlow(Composite parent, AbstractBattle battle) {
		//本次战斗结束后,所有船的状态
		Function<double[], String> percentToString = per -> {
			StringBuilder sb = new StringBuilder();
			sb.append("(");
			sb.append(String.format("%1.4f", per[1])).append(":").append(String.format("%1.4f", per[0]));
			if (per[0] != 0 && per[1] != 1 && per[1] != 0) sb.append(String.format("=%1.4f", per[1] / per[0]));
			sb.append(")");
			return sb.toString();
		};
		SwtUtils.initLabel(new Label(parent, SWT.LEFT),
				"战斗结束-各船状态" +//只有昼战以及开幕夜战才表示受损率
						((battle.isMidnight() && ((AbstractBattleMidnight) battle).isMidnightOnly() == false) ? "" : percentToString.apply(getBattleDamagePercent(battle)))//
				, new GridData(GridData.FILL_HORIZONTAL));
		addShipState(parent, battle);

		//与战斗相关的一些信息
		SwtUtils.insertBlank(parent);
		SwtUtils.initLabel(new Label(parent, SWT.LEFT), "战斗信息", new GridData(GridData.FILL_HORIZONTAL));
		addBattleInformation(parent, battle);

		SwtUtils.insertBlank(parent);
		if (battle instanceof AbstractBattleMidnight) {
			SwtUtils.initLabel(new Label(parent, SWT.CENTER), "夜战", new GridData(GridData.FILL_HORIZONTAL));
			//夜战只有一个stage,所以只加入详细的攻击流程
			AbstractBattleMidnight midnight = ((AbstractBattleMidnight) battle);
			addBattleAttack(parent, battle, midnight.battleMidnightStage.battleAttacks, enemyAttack -> Boolean.FALSE);
		}
		if (battle instanceof AbstractBattleDay) {
			SwtUtils.initLabel(new Label(parent, SWT.CENTER), "昼战", new GridData(GridData.FILL_HORIZONTAL));

			AbstractBattleDay day = ((AbstractBattleDay) battle);
			BattleDeckAttackDamage fbdad = new BattleDeckAttackDamage();
			BattleDeckAttackDamage fbdadco = new BattleDeckAttackDamage();
			BattleDeckAttackDamage ebdad = new BattleDeckAttackDamage();
			BattleDeckAttackDamage ebdadco = new BattleDeckAttackDamage();
			for (int index = 0; index < day.battleDayStage.size(); index++) {
				BattleDayStage stage = day.battleDayStage.get(index);
				//昼战stage的name
				SwtUtils.initLabel(new Label(parent, SWT.LEFT), String.format("%d.%s", index + 1, stage.getStageName()), new GridData(GridData.FILL_HORIZONTAL));

				//有无详细的攻击信息
				boolean haveAttack = false;
				//详细的攻击流程,仅有,开幕对潜,开幕雷击,炮击战,雷击战
				if (stage instanceof OpeningTaisen) {
					haveAttack = true;
					addBattleAttack(parent, battle, stage.battleAttacks, ((OpeningTaisen) stage)::getSimulatorObject);
				} else if (stage instanceof OpeningAttack) {
					haveAttack = true;
					addRaigekiAttack(parent, day, (OpeningAttack) stage);
				}

				//喷气机受损情况
				if (stage instanceof InjectionKouko) {
					String text;
					InjectionKouko ik = (InjectionKouko) stage;
					Function<int[], String> getPLSString = pls -> pls == null ? "" : (pls[0] + "→" + (pls[0] - pls[1]));

					int[][] planeLostStage1 = ik.getPlaneLostStage1();
					int[][] planeLostStage2 = ik.getPlaneLostStage2();

					text = "自:" + getPLSString.apply(planeLostStage1[0]) + "," + getPLSString.apply(planeLostStage2[0]);
					SwtUtils.initLabel(new Label(parent, SWT.LEFT), text, new GridData(GridData.FILL_HORIZONTAL));

					text = "敌:" + getPLSString.apply(planeLostStage1[1]) + "," + getPLSString.apply(planeLostStage2[1]);
					SwtUtils.initLabel(new Label(parent, SWT.LEFT), text, new GridData(GridData.FILL_HORIZONTAL));
				}

				//stage结束后,各船状态
				Composite deckNowState = new Composite(parent, SWT.NONE);
				deckNowState.setLayout(new RowLayout());
				{
					addOnedeckNowState(deckNowState, day.getfDeck(), fbdad, stage.fAttackDamage, haveAttack);
					fbdad.add(stage.fAttackDamage);

					addOnedeckNowState(deckNowState, day.getfDeckCombine(), fbdadco, stage.fAttackDamageco, haveAttack);
					fbdadco.add(stage.fAttackDamageco);

					addOnedeckNowState(deckNowState, day.geteDeck(), ebdad, stage.eAttackDamage, haveAttack);
					ebdad.add(stage.eAttackDamage);

					addOnedeckNowState(deckNowState, day.geteDeckCombine(), ebdadco, stage.eAttackDamageco, haveAttack);
					ebdadco.add(stage.eAttackDamageco);
				}
			}
		}
	}

	private static void addShipState(Composite parent, AbstractBattle battle) {
		addOneShipState(parent, "自-主力舰队", battle.getfDeck(), battle.getfDeckAttackDamage());
		addOneShipState(parent, "自-随从舰队", battle.getfDeckCombine(), battle.getfDeckCombineAttackDamage());
		addOneShipState(parent, "敌-主力舰队", battle.geteDeck(), battle.geteDeckAttackDamage());
		addOneShipState(parent, "敌-随从舰队", battle.geteDeckCombine(), battle.geteDeckCombineAttackDamage());
	}

	private static void addOneShipState(Composite parent, String deckname, BattleDeck bd, BattleDeckAttackDamage bdad) {
		if (AbstractBattle.existBattleDeck(bd) == false) return;
		if (bdad == null) return;

		String[] headers = { deckname, "状态", "先前", "伤害", "当前", "状态", "攻击" };
		int len = headers.length;

		Composite stateComposite = new Composite(parent, SWT.BORDER);
		stateComposite.setLayout(SwtUtils.makeGridLayout(len, 4, 0, 0, 0));

		Composite[] parts = new Composite[len];
		for (int i = 0; i < len; i++) {
			Composite part = parts[i] = new Composite(stateComposite, SWT.NONE);
			part.setLayout(SwtUtils.makeGridLayout(1, 0, 0, 0, 0));
			addNewLabel(part, headers[i]);
		}

		String[] names = bd.names;
		int[] nowhps = bd.nowhps;
		int[] maxhps = bd.maxhps;
		int[] dmgs = bdad.dmgs;
		int[] attacks = bdad.attack;
		for (int i = 0; i < 6; i++) {
			if (maxhps[i] == -1) continue;

			String name = names[i];
			int before = nowhps[i];
			int max = maxhps[i];
			int dmg = dmgs[i];
			int attack = attacks[i];

			String state;
			Color color;
			int after = before - dmg;
			if (after < 0) after = 0;
			{
				addNewLabel(parts[0], name);

				state = bd.escapes.contains(i) ? HPMessage.ESCAPE_STRING : HPMessage.getString(before * 1.0 / max);
				color = HPMessage.getColor(state);
				addNewLabel(parts[1], state, color);

				addNewLabel(parts[2], before + "/" + max);
				addNewLabel(parts[3], "" + dmg);
				addNewLabel(parts[4], after + "/" + max);

				state = bd.escapes.contains(i) ? HPMessage.ESCAPE_STRING : HPMessage.getString(after * 1.0 / max);
				color = HPMessage.getColor(state);
				addNewLabel(parts[5], state, color);

				addNewLabel(parts[6], "" + attack);
			}
		}
	}

	private static void addBattleInformation(Composite parent, AbstractBattle battle) {
		Composite composite = new Composite(parent, SWT.BORDER);
		composite.setLayout(new RowLayout());

		FunctionUtils.notNull(battle.getHangxiang(), hangxiang -> addOneBattleInformation(composite, "航向", hangxiang));
		FunctionUtils.notNull(battle.getZhenxin(), zhenxin -> addOneBattleInformation(composite, "阵型", zhenxin[0], zhenxin[1]));
		FunctionUtils.notNull(battle.getSearch(), search -> addOneBattleInformation(composite, "索敌", search[0], search[1]));

		if (battle instanceof AbstractBattleDay) {
			AbstractBattleDay day = (AbstractBattleDay) battle;
			day.battleDayStage.forEach(stage -> {
				if (stage instanceof Kouko) {
					Predicate<int[]> hasPlane = pls -> pls != null && pls[0] != 0;
					Function<int[], String> getPLSString = pls -> pls == null ? "" : (pls[0] + "→" + (pls[0] - pls[1]));
					Kouko kouko = (Kouko) stage;

					FunctionUtils.notNull(kouko.getSeiku(), seiku -> addOneBattleInformation(composite, "制空", seiku));
					FunctionUtils.notNull(kouko.getTouchPlane(), tp -> {
						if (tp[0] == true || tp[1] == true) {
							addOneBattleInformation(composite, "触接", tp[0] ? "有" : "", tp[1] ? "有" : "");
						}
					});
					if (kouko.getStages()[0]) {
						int[][] pls1 = kouko.getPlaneLostStage1();
						if (hasPlane.test(pls1[0]) || hasPlane.test(pls1[1])) {
							addOneBattleInformation(composite, "stage1", getPLSString.apply(pls1[0]), getPLSString.apply(pls1[1]));
						}
					}
					if (kouko.getStages()[1]) {
						int[][] pls2 = kouko.getPlaneLostStage2();
						if (hasPlane.test(pls2[0]) || hasPlane.test(pls2[1])) {
							addOneBattleInformation(composite, "stage2", getPLSString.apply(pls2[0]), getPLSString.apply(pls2[1]));
						}

						//对空ci
						int[] duikongci = kouko.getDuikongci();
						if (duikongci != null) {
							BattleDeck fdc = day.getfDeckCombine();
							String name = ArrayUtils.addAll(day.getfDeck().names, AbstractBattle.existBattleDeck(fdc) ? fdc.names : AppConstants.EMPTY_NAMES)[duikongci[0]];
							addOneBattleInformation(composite, "对空CI", name, String.valueOf(duikongci[1]));
						}
					}
				}
			});
		}
		if (battle instanceof AbstractBattleMidnight) {
			AbstractBattleMidnight mid = (AbstractBattleMidnight) battle;
			FunctionUtils.notNull(mid.getTouchPlane(), tp -> {
				if (tp[0] == true || tp[1] == true) {
					addOneBattleInformation(composite, "触接", tp[0] ? "有" : "", tp[1] ? "有" : "");
				}
			});
			FunctionUtils.notNull(mid.getFlare(), flare -> {
				if (flare[0] == true || flare[1] == true) {
					addOneBattleInformation(composite, "照明弹", flare[0] ? "有" : "", flare[1] ? "有" : "");
				}
			});
		}

		if (composite.getChildren().length == 0) {
			composite.setLayout(SwtUtils.makeGridLayout(1, 0, 0, 0, 0));
			addNewLabel(composite, "无");
		}
	}

	private static void addOneBattleInformation(Composite parent, String... infos) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(SwtUtils.makeGridLayout(1, 0, 0, 0, 0));
		FunctionUtils.forEach(infos, info -> addNewLabel(composite, info));
	}

	private static void addBattleAttack(Composite parent, AbstractBattle battle, ArrayList<BattleOneAttack> battleAttacks, Function<Boolean, Boolean> fun) {
		if (battleAttacks.stream().mapToInt(ba -> ba.attackIndex).filter(i -> i > 0).count() == 0) {
			return;
		}

		String[] headers = { "攻击方", "攻击类型", "防御方", "伤害", "剩余", "状态" };
		int len = headers.length;

		Composite composite = new Composite(parent, SWT.BORDER);
		composite.setLayout(SwtUtils.makeGridLayout(len, 4, 0, 0, 0));

		Composite[] parts = new Composite[len];
		for (int i = 0; i < len; i++) {
			Composite part = parts[i] = new Composite(composite, SWT.NONE);
			part.setLayout(SwtUtils.makeGridLayout(1, 0, 0, 0, 0));
			addNewLabel(part, headers[i]);
		}

		battleAttacks.forEach(oneAttack -> addOneBattleAttack(parts, battle, oneAttack, fun));
	}

	private static void addOneBattleAttack(Composite[] parts, AbstractBattle battle, BattleOneAttack oneAttack, Function<Boolean, Boolean> fun) {
		Boolean enemyAttack = oneAttack.enemyAttack;
		Boolean fcombine = fun.apply(enemyAttack);
		int attackIndex = oneAttack.attackIndex;
		int attackType = oneAttack.attackType;
		int[] defenseIndexs = oneAttack.defenseIndexs;
		int[] damages = oneAttack.dmgs;

		String[] atters = null, dmgers = null;
		if (enemyAttack == null && fcombine != null) {//敌方非联合舰队
			if (battle.isMidnight()) {
				AbstractBattleMidnight midnight = (AbstractBattleMidnight) battle;
				atters = ArrayUtils.addAll(midnight.getActiveDeck()[0].names, midnight.getActiveDeck()[1].names);
			} else {
				atters = ArrayUtils.addAll(fcombine == Boolean.TRUE ? battle.getfDeckCombine().names : battle.getfDeck().names, battle.geteDeck().names);
			}
			dmgers = atters;
		} else if (enemyAttack == Boolean.FALSE) {//敌联合舰队,我方攻击
			atters = ArrayUtils.addAll(battle.getfDeck().names, battle.getfDeckCombine().names);
			dmgers = ArrayUtils.addAll(battle.geteDeck().names, battle.geteDeckCombine().names);
		} else if (enemyAttack == Boolean.TRUE) {//敌联合舰队,敌方攻击
			atters = ArrayUtils.addAll(battle.geteDeck().names, battle.geteDeckCombine().names);
			dmgers = ArrayUtils.addAll(battle.getfDeck().names, battle.getfDeckCombine().names);
		}

		for (int i = 0; i < defenseIndexs.length; i++) {
			if (defenseIndexs[i] == -1) continue;

			String atter = atters == null ? "" : atters[attackIndex - 1];
			String dmger = dmgers == null ? "" : dmgers[defenseIndexs[i] - 1];
			String type = getBattleAttackType(battle.isMidnight(), attackType);
			if (i == 0) {
				addNewLabel(parts[0], atter);
				addNewLabel(parts[1], type);
				addNewLabel(parts[2], dmger);
			} else {
				addNewLabel(parts[0], "");
				addNewLabel(parts[1], "");
				addNewLabel(parts[2], "");
			}

			addNewLabel(parts[3], String.valueOf(damages[i]));
			addNewLabel(parts[4], "");
			addNewLabel(parts[5], "");
		}
	}

	private static void addRaigekiAttack(Composite parent, AbstractBattleDay day, OpeningAttack raigeki) {
		//frai与fydam长度相同
		int[] frai = raigeki.frai;//目标
		int[] erai = raigeki.erai;
		int[] fdam = raigeki.fdam;//伤害
		int[] edam = raigeki.edam;
		int[] fydam = raigeki.fydam;//攻击
		int[] eydam = raigeki.eydam;

		//有雷击战但无攻击
		if (Arrays.stream(frai).filter(i -> i > 0).count() == 0 && Arrays.stream(erai).filter(i -> i > 0).count() == 0) {
			return;
		}

		String[] headers = { "攻击方", "防御方", "伤害" };
		int len = headers.length;

		Composite composite = new Composite(parent, SWT.BORDER);
		composite.setLayout(SwtUtils.makeGridLayout(len, 4, 0, 0, 0));

		Composite[] parts = new Composite[len];
		for (int i = 0; i < len; i++) {
			Composite part = new Composite(composite, SWT.NONE);
			part.setLayout(SwtUtils.makeGridLayout(1, 0, 0, 0, 0));
			addNewLabel(part, headers[i]);
			parts[i] = part;
		}

		//自方雷击
		{
			for (int i = 1; i < frai.length; i++) {
				if (frai[i] <= 0) continue;

				int attackindex = i - 1;
				int defenseindex = frai[i] - 1;
				String attacker = "", defenser = "", dmg = String.valueOf(fydam[i]);

				switch (frai.length) {
					case 1 + 12:
						attacker = ArrayUtils.addAll(day.getfDeck().names, day.getfDeckCombine().names)[attackindex];
						break;
					case 1 + 6:
						if (day.getfDeckCombine() == null) {
							attacker = day.getfDeck().names[attackindex];
						} else {
							attacker = day.getfDeckCombine().names[attackindex];
						}
						break;
				}
				switch (edam.length) {
					case 1 + 12:
						defenser = ArrayUtils.addAll(day.geteDeck().names, day.geteDeckCombine().names)[defenseindex];
						break;
					case 1 + 6:
						if (day.geteDeckCombine() == null) {
							defenser = day.geteDeck().names[defenseindex];
						} else {
							defenser = day.geteDeckCombine().names[defenseindex];
						}
						break;
				}

				addNewLabel(parts[0], attacker);
				addNewLabel(parts[1], defenser);
				addNewLabel(parts[2], dmg);
			}
		}
		//敌方雷击
		{
			for (int i = 1; i < erai.length; i++) {
				if (erai[i] <= 0) continue;

				int attackindex = i - 1;
				int defenseindex = erai[i] - 1;
				String attacker = "", defenser = "", dmg = String.valueOf(eydam[i]);

				switch (erai.length) {
					case 1 + 12:
						attacker = ArrayUtils.addAll(day.geteDeck().names, day.geteDeckCombine().names)[attackindex];
						break;
					case 1 + 6:
						if (day.geteDeckCombine() == null) {
							attacker = day.geteDeck().names[attackindex];
						} else {
							attacker = day.geteDeckCombine().names[attackindex];
						}
						break;
				}
				switch (fdam.length) {
					case 1 + 12:
						defenser = ArrayUtils.addAll(day.getfDeck().names, day.getfDeckCombine().names)[defenseindex];
						break;
					case 1 + 6:
						if (day.getfDeckCombine() == null) {
							defenser = day.getfDeck().names[defenseindex];
						} else {
							defenser = day.getfDeckCombine().names[defenseindex];
						}
						break;
				}

				addNewLabel(parts[0], attacker);
				addNewLabel(parts[1], defenser);
				addNewLabel(parts[2], dmg);
			}
		}
	}

	private static void addOnedeckNowState(Composite parent, BattleDeck bd, BattleDeckAttackDamage bdad, BattleDeckAttackDamage ad, boolean haveAttack) {
		if (AbstractBattle.existBattleDeck(bd) == false) return;
		if (bdad == null) return;

		String[] headers = haveAttack ? new String[] { "", "状态", "当前", "伤害", "攻击" } : new String[] { "", "状态", "当前", "伤害" };
		int len = headers.length;

		Composite composite = new Composite(parent, SWT.BORDER);
		composite.setLayout(SwtUtils.makeGridLayout(len, 4, 0, 0, 0));

		Composite[] parts = new Composite[len];
		for (int i = 0; i < len; i++) {
			Composite part = parts[i] = new Composite(composite, SWT.NONE);
			part.setLayout(SwtUtils.makeGridLayout(1, 0, 0, 0, 0));
			addNewLabel(part, headers[i]);
		}

		String[] names = bd.names;
		int[] nowhps = bd.nowhps;
		int[] maxhps = bd.maxhps;
		int[] dmg1 = bdad.dmgs;
		int[] atts = ad.attack;
		int[] dmg2 = ad.dmgs;
		for (int i = 0; i < 6; i++) {
			if (nowhps[i] == -1) continue;

			String name = names[i];
			int max = maxhps[i];
			int now = nowhps[i] - dmg1[i] - dmg2[i];
			if (now < 0) now = 0;
			int att = atts[i];
			int dmg = dmg2[i];
			{
				Label nameLabel = new Label(parts[0], SWT.CENTER);
				SwtUtils.initLabel(nameLabel, name, new GridData(SWT.CENTER, SWT.CENTER, false, false), 55);
				FunctionUtils.notNull(name, nameLabel::setToolTipText);

				String state = bd.escapes.contains(i) ? HPMessage.ESCAPE_STRING : HPMessage.getString(now * 1.0 / max);
				Color color = HPMessage.getColor(state);
				addNewLabel(parts[1], state, color);

				addNewLabel(parts[2], now + "/" + max);
				addNewLabel(parts[3], "" + dmg);
				if (haveAttack) addNewLabel(parts[4], "" + att);
			}
		}
	}

	private static void addNewLabel(Composite parent, String text) {
		SwtUtils.initLabel(new Label(parent, SWT.CENTER), text, new GridData(SWT.CENTER, SWT.CENTER, false, false));
	}

	private static void addNewLabel(Composite parent, String text, Color color) {
		SwtUtils.initLabel(new Label(parent, SWT.CENTER), text, new GridData(SWT.CENTER, SWT.CENTER, false, false), color);
	}

	/*------------------------------------------------------------------------------*/

	public static boolean haveDamage(AbstractBattle battle) {
		Predicate<BattleDeckAttackDamage> haveDamage = bdad -> bdad == null ? false : Arrays.stream(bdad.dmgs).anyMatch(i -> i != 0);
		return haveDamage.test(battle.getfDeckAttackDamage()) || haveDamage.test(battle.getfDeckCombineAttackDamage());
	}

	public static String getBattleAttackType(boolean isMidnight, int attackType) {
		if (isMidnight) {
			switch (attackType) {
				case 0://普通单击
					return "";
				case 1:
					return "二连";
				case 2:
					return "炮雷CI";
				case 3:
					return "鱼雷CI";
				case 4:
					return "主副CI";
				case 5:
					return "主主CI";
			}
		} else {
			switch (attackType) {
				case 0://普通单击
					return "";
				case 2:
					return "二连";
				case 3:
					return "主副CI";
				case 4:
					return "主电CI";
				case 5:
					return "主撤CI";
				case 6:
					return "主主CI";
			}
		}
		return String.valueOf(attackType);
	}

	public static double[] getBattleDamagePercent(AbstractBattle battle) {
		BiFunction<BattleDeck, BattleDeckAttackDamage, int[]> get = (bd, bdad) -> {
			int tnow = 0, tdmg = 0;
			if (AbstractBattle.existBattleDeck(bd) && bdad != null) {
				int[] nowhps = bd.nowhps;
				int[] dmgs = bdad.dmgs;
				for (int i = 0; i < 6; i++) {
					int now = nowhps[i];
					int dmg = dmgs[i];
					if (now < 0) continue;

					tnow += now;
					tdmg += dmg > now ? now : dmg;
				}
			}
			return new int[] { tnow, tdmg };
		};

		int fnow = 0, enow = 0;
		int fdmg = 0, edmg = 0;
		for (int[] nd : new int[][] { //
				get.apply(battle.getfDeck(), battle.getfDeckAttackDamage()),//
				get.apply(battle.getfDeckCombine(), battle.getfDeckCombineAttackDamage()),//
		}) {
			fnow += nd[0];
			fdmg += nd[1];
		}
		for (int[] nd : new int[][] { //
				get.apply(battle.geteDeck(), battle.geteDeckAttackDamage()),//
				get.apply(battle.geteDeckCombine(), battle.geteDeckCombineAttackDamage()),//
		}) {
			enow += nd[0];
			edmg += nd[1];
		}

		DoubleBinaryOperator calcu = (now, dmg) -> dmg == 0 ? 0 : (dmg * 1.0 / now);
		return new double[] { calcu.applyAsDouble(fnow, fdmg), calcu.applyAsDouble(enow, edmg) };
	}

}
