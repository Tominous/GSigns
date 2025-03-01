package de.stylextv.gs.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import de.stylextv.gs.main.Main;
import de.stylextv.gs.main.Variables;

public class AutoUpdater {
	
	private Main plugin;
	
	private String updateRequest;
	private boolean inUpdateCheck;
	
	public AutoUpdater(Main plugin) {
		this.plugin = plugin;
	}
	
	public void startAutoUpdater() {
		if(updateRequest!=null) try {
			URL url = new URL("https://github.com/StylexTV/GSigns/raw/master/version/"+updateRequest+".jar");
			ReadableByteChannel rbc = Channels.newChannel(url.openStream());
			
			FileOutputStream fos = new FileOutputStream(plugin.getPluginFile());
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
			fos.close();
			
			BufferedWriter writer = new BufferedWriter(new FileWriter("plugins/GSigns/au-result"));
			writer.write(Variables.VERSION);
		    writer.close();
		} catch(Exception ex) {ex.printStackTrace();}
	}
	public void checkAutoUpdater() {
		File f=new File("plugins/GSigns/au-result");
		if(f.exists()) {
			new BukkitRunnable() {
				@Override
				public void run() {
					String past=null;
					try {
						BufferedReader reader = new BufferedReader(new FileReader(f));
						past=reader.readLine();
						reader.close();
					} catch (IOException ex) {ex.printStackTrace();}
					f.delete();
					
					final String pastF=past;
					new BukkitRunnable() {
						@Override
						public void run() {
							Bukkit.getConsoleSender().sendMessage(Variables.PREFIX_CONSOLE+"A new update has been §ainstalled§r. Version: "+pastF+" -> "+Variables.VERSION);
							Bukkit.getConsoleSender().sendMessage(Variables.PREFIX_CONSOLE+"The changelog can be found here:");
							Bukkit.getConsoleSender().sendMessage(Variables.PREFIX_CONSOLE+"https://www.spigotmc.org/resources/g-signs-a-unique-map-signs-plugin-for-lobbies.73693/updates");
						}
					}.runTask(plugin);
				}
			}.runTaskLaterAsynchronously(plugin, 5);
		}
		
		new BukkitRunnable() {
			@Override
			public void run() {
				try {
					int currentVersion=(int) (Double.valueOf(Variables.VERSION)*10);
					int future=1;
					String found=null;
					while(future<100) {
						int i=currentVersion+future;
						try {
							String fileUrl=i/10+"."+i%10;
							URL url = new URL("https://github.com/StylexTV/GSigns/raw/master/version/"+fileUrl+".jar");
							ReadableByteChannel rbc = Channels.newChannel(url.openStream());
							rbc.close();
							found=fileUrl;
						} catch(Exception ex) {
							break;
						}
					    
					    future++;
					}
					
					if(found!=null) {
						final String foundF=found;
						new BukkitRunnable() {
							@Override
							public void run() {
								Bukkit.getConsoleSender().sendMessage(Variables.PREFIX_CONSOLE+"A new §aupdate§r has been found. Version: "+foundF);
								Bukkit.getConsoleSender().sendMessage(Variables.PREFIX_CONSOLE+"Enter §e/gs update§r into your ingame chat to install the update.");
							}
						}.runTask(plugin);
					}
				} catch(Exception ex) {
					new BukkitRunnable() {
						@Override
						public void run() {
							Bukkit.getConsoleSender().sendMessage(Variables.PREFIX_CONSOLE+"An exception occurred while §cchecking for new updates§r:");
							ex.printStackTrace();
						}
					}.runTask(plugin);
				}
			}
		}.runTaskLaterAsynchronously(plugin, 5);
	}
	
	public void runAutoUpdater(Player p) {
		if(updateRequest!=null) {
			p.sendMessage(Variables.PREFIX+"§7A new update has §ealready§7 been found. Version: "+updateRequest);
			p.sendMessage(Variables.PREFIX+"§7The update is installed when the server is §eclosed §7or §erestarted§7.");
		} else if(inUpdateCheck) {
			p.sendMessage(Variables.PREFIX+"§eSomeone else§7 is already checking for an update.");
		} else {
			inUpdateCheck=true;
			p.sendMessage(Variables.PREFIX+"§7Checking for new updates...");
			BukkitTask runnable=new BukkitRunnable() {
				@Override
				public void run() {
					p.sendMessage(Variables.PREFIX+"§7...");
				}
			}.runTaskTimerAsynchronously(plugin, 120, 120);
			new BukkitRunnable() {
				@Override
				public void run() {
					try {
						int currentVersion=(int) (Double.valueOf(Variables.VERSION)*10);
						int future=1;
						String found=null;
						boolean noConnection=false;
						while(future<100) {
							int i=currentVersion+future;
							try {
								String fileUrl=i/10+"."+i%10;
								URL url = new URL("https://github.com/StylexTV/GSigns/raw/master/version/"+fileUrl+".jar");
								ReadableByteChannel rbc = Channels.newChannel(url.openStream());
								rbc.close();
								found=fileUrl;
							} catch(Exception ex) {
								if(!(ex instanceof FileNotFoundException)) {
									noConnection=true;
								}
								break;
							}
						    
						    future++;
						}
						
						if(noConnection) {
							p.sendMessage(Variables.PREFIX+"§7Couldn't connect to the server. Make sure you are connected to the §cinternet§7.");
						} else if(found!=null) {
							updateRequest=found;
							p.sendMessage(Variables.PREFIX+"§8§m⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯");
							p.sendMessage(Variables.PREFIX+"§7A new update has been §afound§7. Version: "+found);
							p.sendMessage(Variables.PREFIX+"§7The update is installed when the server is");
							p.sendMessage(Variables.PREFIX+"§eclosed §7or §erestarted§7.");
							p.sendMessage(Variables.PREFIX+"§8§m⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯⎯");
						} else {
							p.sendMessage(Variables.PREFIX+"§7The plugin is up to date! You are running the §alatest§7 version of "+Variables.NAME+".");
						}
					} catch(Exception ex) {
						p.sendMessage(Variables.PREFIX+"§7An exception occurred while §cchecking for new updates§7!");
						ex.printStackTrace();
					}
					inUpdateCheck=false;
					runnable.cancel();
				}
			}.runTaskLaterAsynchronously(plugin, 5);
		}
	}
	
}
