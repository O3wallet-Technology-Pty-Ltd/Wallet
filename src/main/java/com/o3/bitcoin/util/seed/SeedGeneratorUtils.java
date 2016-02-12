/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.o3.bitcoin.util.seed;

import com.o3.bitcoin.exception.SeedPhraseException;
import org.bitcoinj.crypto.MnemonicCode;
import org.bitcoinj.crypto.MnemonicException;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>Generator to provide the following to UI code:</p>
 * <ul>
 * <li>Generation of BIP39 seed phrases</li>
 * <li>Validation of BIP39 seed phrases</li>
 * <li>Conversion of BIP39 seed phrases to and from seed byte value</li>
 * </ul>
 * </p>
 *
 */
public class SeedGeneratorUtils {

  private MnemonicCode mnemonicCode;
  private SecureRandom secureRandom;

  public SeedGeneratorUtils() throws SeedPhraseException {
    try {
      mnemonicCode = new MnemonicCode();
      secureRandom = new SecureRandom();
    } catch (IOException ioe) {
      throw new SeedPhraseException("Could not initialise Bip39SeedPhraseGenerator", ioe);
    }
  }

  public static List<String> split(String words) {
    return new ArrayList<>(Arrays.asList(words.split("\\s+")));
  }

  public List<String> newSeedPhrase() throws SeedPhraseException {
    return newSeedPhrase(SeedPhraseSize.TWELVE_WORDS);
  }

  public List<String> newSeedPhrase(SeedPhraseSize size) throws SeedPhraseException {
    try {
      byte[] entropy = new byte[size.getEntropyBytesSize()];
      secureRandom.nextBytes(entropy);
      return mnemonicCode.toMnemonic(entropy);
    } catch (MnemonicException.MnemonicLengthException mle) {
      throw new SeedPhraseException("Wrong length of entropy bytes", mle);
    }
  }

  public byte[] convertToSeed(List<String> seedPhrase) throws SeedPhraseException {
    try {
      mnemonicCode.check(seedPhrase);

      // Convert to seed byte array using an empty credentials
      return MnemonicCode.toSeed(seedPhrase, "");
    } catch (MnemonicException e) {
      throw new SeedPhraseException("The seed phrase is not valid", e);
    }
  }

  public boolean isValid(List<String> seedPhrase) {

    if (SeedPhraseSize.isValid(seedPhrase.size())) {
      try {
        mnemonicCode.check(seedPhrase);
        return true;

      } catch (MnemonicException e) {
        // Do nothing
      }
    }

    // Must have failed to be here
    return false;

  }
}

