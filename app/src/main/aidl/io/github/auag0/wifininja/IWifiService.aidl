package io.github.auag0.wifininja;

import io.github.auag0.wifininja.models.WifiConfigurationWrapper;

interface IWifiService {
    List<WifiConfigurationWrapper> getConfiguredNetworks();
    int addNetwork(in WifiConfigurationWrapper wifiConfigWrapper);
}