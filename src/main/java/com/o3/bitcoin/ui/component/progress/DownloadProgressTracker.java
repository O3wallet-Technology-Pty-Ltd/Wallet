package com.o3.bitcoin.ui.component.progress;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.o3.bitcoin.service.WalletService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingUtilities;
import org.bitcoinj.core.AbstractPeerEventListener;
import org.bitcoinj.core.Block;
import org.bitcoinj.core.Peer;
import org.bitcoinj.core.Utils;

/**
 * <p>
 * An implementation of {@link AbstractPeerEventListener} that listens to chain
 * download events and tracks progress as a percentage.</p>
 */
public class DownloadProgressTracker extends AbstractPeerEventListener {

    private static final Logger log = LoggerFactory.getLogger(DownloadProgressTracker.class);
    private int originalBlocksLeft = -1;
    private int lastPercent = 0;
    private SettableFuture<Long> future = SettableFuture.create();
    private boolean caughtUp = false;

    private final WalletService walletService;

    public DownloadProgressTracker(WalletService walletService) {
        this.walletService = walletService;
    }

    @Override
    public void onChainDownloadStarted(Peer peer, int blocksLeft) {
        if (blocksLeft > 0 && originalBlocksLeft == -1) {
            startDownload(blocksLeft);
        }
        // Only mark this the first time, because this method can be called more than once during a chain download
        // if we switch peers during it.
        if (originalBlocksLeft == -1) {
            originalBlocksLeft = blocksLeft;
        } else {
            log.info("Chain download switched to {}", peer);
        }
        if (blocksLeft == 0) {
            doneDownload();
            future.set(peer.getBestHeight());
        }
    }

    @Override
    public void onBlocksDownloaded(Peer peer, Block block, int blocksLeft) {
        if (caughtUp) {
            return;
        }

        if (blocksLeft == 0) {
            caughtUp = true;
            doneDownload();
            future.set(peer.getBestHeight());
        }

        if (blocksLeft < 0 || originalBlocksLeft <= 0) {
            return;
        }

        double pct = 100.0 - (100.0 * (blocksLeft / (double) originalBlocksLeft));
        if ((int) pct != lastPercent) {
            progress(pct, blocksLeft, new Date(block.getTimeSeconds() * 1000));
            lastPercent = (int) pct;
        }
    }

//    @Override
//    public void onBlocksDownloaded(Peer peer, Block block, FilteredBlock filteredBlock, int blocksLeft) {
//        
//    }
    /**
     * Called when download progress is made.
     *
     * @param pct the percentage of chain downloaded, estimated
     * @param blocksSoFar
     * @param date the date of the last block downloaded
     */
    protected void progress(final double pct, int blocksSoFar, Date date) {
        log.info(String.format("Chain download %d%% done with %d blocks to go, block date %s", (int) pct, blocksSoFar,
                Utils.dateTimeFormat(date)));
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                walletService.updateNetworkSyncPct(pct / 100.0);
            }
        });
    }

    /**
     * Called when download is initiated.
     *
     * @param blocks the number of blocks to download, estimated
     */
    protected void startDownload(int blocks) {
        log.info("Downloading block chain of size " + blocks + ". "
                + (blocks > 1000 ? "This may take a while." : ""));
    }

    /**
     * Called when we are done downloading the block chain.
     */
    protected void doneDownload() {
        log.info("Block download completed ...");
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                walletService.networkSyncCompleted();
            }
        });
    }

    /**
     * Wait for the chain to be downloaded.
     */
    public void await() throws InterruptedException {
        try {
            future.get();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns a listenable future that completes with the height of the best
     * chain (as reported by the peer) once chain download seems to be finished.
     */
    public ListenableFuture<Long> getFuture() {
        return future;
    }
}
