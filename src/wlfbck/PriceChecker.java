package wlfbck;

import java.awt.AWTException;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.Robot;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

public class PriceChecker implements NativeKeyListener {

	private TrayIcon trayIcon;
	Robot robot;
	Gson gson;

	private final String currentLeague = "Flashback+Event+(BRE001)";//"Bestiary";
	private final String ninjaArmourLink = "http://poe.ninja/api/Data/GetUniqueArmourOverview?league=";
	private final String ninjaWeaponLink = "http://poe.ninja/api/Data/GetUniqueWeaponOverview?league=";
	private final String ninjaFlaskLink = "http://poe.ninja/api/Data/GetUniqueFlaskOverview?league=";
	private final String ninjaAccessoryLink = "http://poe.ninja/api/Data/GetUniqueAccessoryOverview?league=";
	private final String ninjaJewelLink = "http://poe.ninja/api/Data/GetUniqueJewelOverview?league=";
	private final String ninjaDivinationCardLink = "http://poe.ninja/api/Data/GetDivinationCardsOverview?league=";
	private final String ninjaMapLink = "http://poe.ninja/api/Data/GetUniqueMapOverview?league=";

	private List<Item> allUniqueItems = new LinkedList<>();

	private int hotkeyCode = 61; // F3

	public static void main(String[] args) {
		// For KeyListener
		Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
		logger.setLevel(Level.OFF);

		logger.setUseParentHandlers(false);

		try {
			GlobalScreen.registerNativeHook();
		} catch (NativeHookException ex) {
			System.err.println("There was a problem registering the native hook.");
			System.err.println(ex.getMessage());

			System.exit(1);
		}

		try {
			PriceChecker pc = new PriceChecker();
		} catch (AWTException ex) {
			System.err.println("Failed to initialize Robot.");
			System.err.println(ex.getMessage());
			System.exit(1);
		}

		try {
			while (true) {
				Thread.sleep(128);
			}
		} catch (InterruptedException e) {
		}
	}

	public PriceChecker() throws AWTException {
		GlobalScreen.addNativeKeyListener(this);
		robot = new Robot();
		gson = new Gson();

		createTrayIcon();

		// get price data every 2 hours and once on startup
		Timer t = new Timer();
		t.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				updatePriceData();
			}
		}, 0, TimeUnit.MILLISECONDS.convert(2, TimeUnit.HOURS));
	}

	public void checkPrice() throws HeadlessException, UnsupportedFlavorException, IOException, InterruptedException {
		robot.keyPress(KeyEvent.VK_CONTROL);
		Thread.sleep(10);
		robot.keyPress(KeyEvent.VK_C);
		Thread.sleep(10);
		robot.keyRelease(KeyEvent.VK_C);
		Thread.sleep(10);
		robot.keyRelease(KeyEvent.VK_CONTROL);
		Thread.sleep(10);
		Item item = parseItem(
				(String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor));
		if (item != null) {
			LinkedList<Double> price = getPriceForItem(item);
			displayPrice(price, item);
		}
	}

	public LinkedList<Double> getPriceForItem(Item item) {
		LinkedList<Double> chaosPrice = new LinkedList<>();
		if (item.name.contains("Map")) {
			for (Item i : allUniqueItems) {
				if(i.baseType != null && i.baseType.equals(item.baseType)) {
					item.name = i.name;
					chaosPrice.add(i.chaosValue);
					System.out.println(i);
				}
			}
		} else {
			for (Item i : allUniqueItems) {
				if (i.name.equals(item.name)) {
					if(i.links>0) {
						continue;
					}
					chaosPrice.add(i.chaosValue);
					System.out.println(i);
				}
			}
		}
		return chaosPrice;
	}

	public void updatePriceData() {
		allUniqueItems.clear();
		Type itemListType = new TypeToken<LinkedList<Item>>() {
		}.getType();

		JsonObject wrapperRemoval;
		try {
			wrapperRemoval = gson.fromJson(readUrl(ninjaArmourLink + currentLeague), JsonObject.class);
			LinkedList<Item> armours = gson.fromJson(((JsonArray) wrapperRemoval.get("lines")), itemListType);
			allUniqueItems.addAll(armours);
			wrapperRemoval = gson.fromJson(readUrl(ninjaWeaponLink + currentLeague), JsonObject.class);
			LinkedList<Item> weapons = gson.fromJson(((JsonArray) wrapperRemoval.get("lines")), itemListType);
			allUniqueItems.addAll(weapons);
			wrapperRemoval = gson.fromJson(readUrl(ninjaFlaskLink + currentLeague), JsonObject.class);
			LinkedList<Item> flasks = gson.fromJson(((JsonArray) wrapperRemoval.get("lines")), itemListType);
			allUniqueItems.addAll(flasks);
			wrapperRemoval = gson.fromJson(readUrl(ninjaAccessoryLink + currentLeague), JsonObject.class);
			LinkedList<Item> accessories = gson.fromJson(((JsonArray) wrapperRemoval.get("lines")), itemListType);
			allUniqueItems.addAll(accessories);
			wrapperRemoval = gson.fromJson(readUrl(ninjaJewelLink + currentLeague), JsonObject.class);
			LinkedList<Item> jewels = gson.fromJson(((JsonArray) wrapperRemoval.get("lines")), itemListType);
			allUniqueItems.addAll(jewels);
			wrapperRemoval = gson.fromJson(readUrl(ninjaDivinationCardLink + currentLeague), JsonObject.class);
			LinkedList<Item> divinationCards = gson.fromJson(((JsonArray) wrapperRemoval.get("lines")), itemListType);
			allUniqueItems.addAll(divinationCards);
			wrapperRemoval = gson.fromJson(readUrl(ninjaMapLink + currentLeague), JsonObject.class);
			LinkedList<Item> maps = gson.fromJson(((JsonArray) wrapperRemoval.get("lines")), itemListType);
			allUniqueItems.addAll(maps);
		} catch (JsonSyntaxException ex) {
			System.err.println("Failed to parse data from poe.ninja?");
			System.err.println(ex.getMessage());
			System.exit(1);
		} catch (IOException ex) {
			System.err.println("Could not get data from poe.ninja.");
			System.err.println(ex.getMessage());
			System.exit(1);
		}
	}

	private void displayPrice(LinkedList<Double> price, Item item) {
		PriceNotification frame = new PriceNotification(price, item);
		frame.setVisible(true);

		new Timer().schedule(new TimerTask() {
			public void run() {
				frame.dispose();
			}
		}, 3000);
	}

	public Item parseItem(String clipboard) {
		Item item = null;
		System.out.println(clipboard);
		String lines[] = clipboard.split("\\r?\\n");
		// Check if copying worked
		if (lines[0] != null) {
			// it's a unique item or divination card
			if ((lines[0].equals("Rarity: Unique") || lines[0].equals("Rarity: Divination Card"))) {
				if (lines[1] != null && lines[2] != null) {
					item = new Item(lines[1]);
				}
			}
			// it's a unique map
			if (lines[0].equals("Rarity: Unique") && lines[1].contains("Map")) {
				item = new Item(lines[1]);
				item.baseType = lines[1];
			}
		}

		return item;
	}

	public String determineType(String baseName) {
		// TODO: pull basedata from somewhere, determine that way...
		// if this is even needed
		return "";
	}

	@Override
	public void nativeKeyTyped(NativeKeyEvent event) {
	}

	@Override
	public void nativeKeyPressed(NativeKeyEvent event) {
		if (event.getKeyCode() == 63) {
			// updatePriceData();
			//displayPrice(new LinkedList<Double>(Arrays.asList(5.0)), new Item("bla"));
		}
		if (event.getKeyCode() == hotkeyCode) {
			try {
				checkPrice();
			} catch (HeadlessException | UnsupportedFlavorException | IOException | InterruptedException ex) {
				System.out.println("Something went really wrong parsing the item...");
				System.err.println(ex.getMessage());
				System.exit(1);
			}
		}
	}

	@Override
	public void nativeKeyReleased(NativeKeyEvent event) {
	}

	private String readUrl(String urlString) throws IOException {
		BufferedReader reader = null;
		try {
			URL url = new URL(urlString);
			reader = new BufferedReader(new InputStreamReader(url.openStream()));
			StringBuffer buffer = new StringBuffer();
			int read;
			char[] chars = new char[1024];
			while ((read = reader.read(chars)) != -1) {
				buffer.append(chars, 0, read);
			}
			return buffer.toString();
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}

	private void createTrayIcon() {
		PopupMenu trayMenu = new PopupMenu();
		MenuItem exit = new MenuItem("Exit");
		exit.addActionListener(e -> {
			System.exit(0);
		});
		trayMenu.add(exit);

		BufferedImage icon = null;
		try {
			System.out.println(getClass().getClassLoader());
			icon = ImageIO.read(getClass().getClassLoader().getResource("bag-147782.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.trayIcon = new TrayIcon(icon, "MercuryTrade", trayMenu);
		this.trayIcon.setImageAutoSize(true);

		SystemTray tray = SystemTray.getSystemTray();
		try {
			tray.add(this.trayIcon);
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}
}