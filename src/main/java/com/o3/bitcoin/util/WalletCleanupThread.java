/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.o3.bitcoin.util;

import com.o3.bitcoin.model.Config;
import com.o3.bitcoin.model.manager.ConfigManager;
import com.o3.bitcoin.service.WalletService;
import java.io.File;
import java.util.Iterator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WalletCleanupThread implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(WalletCleanupThread.class);
    private static final long WAIT_TIME = 10 * 1000; //SECONDS 
    private static final int MAX_TRIES = 5;
    private static int retryCount = 0;
    private WalletService walletService;

    public WalletCleanupThread() {
    }

    public static Thread delete() {
        Thread tRun = new Thread(new WalletCleanupThread());
        tRun.start();
        return tRun;
    }
    
    public Thread deleteWallet(WalletService walletService) {
        this.walletService = walletService;
        Thread tRun = new Thread(this);
        tRun.start();
        return tRun;
    }

    @Override
    public void run() {
        logger.debug("Wallet cleanup thread started > RETRY : {}", retryCount);
        boolean shouldRun = true;
        try {
            Config config = ConfigManager.config();
            if (retryCount < MAX_TRIES) {
                boolean success = false;
                if (walletService != null) {
                    walletService.closeWallet(true);
                    String path = walletService.getWalletConfig().getLocation() + File.separator + walletService.getWalletConfig().getId();
                    success = delete(path);
                } else {
                    List<String> deleted = config.getDeleted();
                    if (deleted != null && !deleted.isEmpty()) {
                        Iterator<String> iterator = deleted.iterator();
                        while (iterator.hasNext()) {
                            success = delete(iterator.next());
                            if (success) {
                                iterator.remove();
                            }
                        }
                    } else {
                        logger.debug("No cleanup is required.");
                        shouldRun = false;
                    }
                }
                if (success) {
                    ConfigManager.get().save();
                    if (config.getDeleted() != null && config.getDeleted().size() > 0) {
                        shouldRun = true;
                    } else {
                        logger.debug("No more cleanup is required.");
                        shouldRun = false;
                    }
                }
            } else {
                logger.debug("Cleanup retried have expired.");
                shouldRun = false;
            }
        } catch (Exception ex) {
            logger.error(null);
        } finally {
            if (retryCount < MAX_TRIES && shouldRun) {
                retryCount++;
                respawn();
            }
        }
    }

    private boolean delete(String path) {
        try {
            File dir = new File(path);
            logger.debug("Removing dir: {}", path);
            if (dir.exists()) {
                boolean success = Utils.deleteDir(new File(path));
                if (success) {
                    logger.debug("Deleted dir : ", path);
                    return true;
                } else {
                    logger.debug("Unable to delet dir : ", path);
                }
            } else {
                logger.debug("Not Exists > dir : {}", path);
                logger.debug("Removed dir : {}", path);
                return true;
            }
        } catch (Exception ex) {
            logger.error("Unable to delete dir {} > Error: {}", path, ex.getMessage(), ex);
        }
        return false;
    }

    private void respawn() {
        try {
            logger.debug("Spawning a new cleanup thread in {} seconds.", WAIT_TIME / 1000);
            Thread.sleep(WAIT_TIME);
            delete();
        } catch (Exception ex) {
            logger.error("Unable to spawn thread : {}", ex.getMessage(), ex);
        }
    }
}
