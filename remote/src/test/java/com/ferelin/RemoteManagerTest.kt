package com.ferelin

import com.ferelin.remote.RemoteManager
import com.ferelin.remote.RemoteManagerHelper
import com.ferelin.remote.network.NetworkManager
import com.ferelin.remote.network.NetworkManagerHelper
import com.ferelin.remote.webSocket.WebSocketConnector
import com.ferelin.remote.webSocket.WebSocketConnectorHelper
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.mockito.Spy
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner::class)
class RemoteManagerTest {

    private lateinit var mRemoteManager: RemoteManagerHelper

    @Spy
    private lateinit var mNetworkManager: NetworkManagerHelper

    @Spy
    private lateinit var mWebSocketConnector: WebSocketConnectorHelper

    @Before
    fun setUp() {
        mNetworkManager = mock(NetworkManager::class.java)
        mWebSocketConnector = mock(WebSocketConnector::class.java)
        mRemoteManager = RemoteManager(mNetworkManager, mWebSocketConnector)
    }

    @Test
    fun openConnection() {
        val token = "token"
        mRemoteManager.openConnection(token)
        verify(mWebSocketConnector, times(1)).openConnection(token)
    }

    @Test
    fun closeConnection() {
        mRemoteManager.closeConnection()
        verify(mWebSocketConnector, times(1)).closeConnection()
    }

    @Test
    fun subscribeItem() {
        val symbol = "symbol"
        val price = 100.0
        mRemoteManager.subscribeItem(symbol, price)
        verify(mWebSocketConnector, times(1)).subscribeItem(symbol, price)
    }

    @Test
    fun unsubscribeItem() {
        val symbol = "symbol"
        mRemoteManager.unsubscribeItem(symbol)
        verify(mWebSocketConnector, times(1)).unsubscribeItem(symbol)
    }

    @Test
    fun loadStockCandles() {
        val symbol = "symbol"
        val from = 100L
        val to = 200L
        val resolution = "D"
        mRemoteManager.loadStockCandles(symbol, from, to, resolution)
        verify(mNetworkManager, times(1)).loadStockCandles(symbol, from, to, resolution)
    }

    @Test
    fun loadCompanyProfile() {
        val symbol = "symbol"
        mRemoteManager.loadCompanyProfile(symbol)
        verify(mNetworkManager, times(1)).loadCompanyProfile(symbol)
    }

    @Test
    fun loadCompanyNews() {
        val symbol = "symbol"
        val from = "from"
        val to = "to"
        mRemoteManager.loadCompanyNews(symbol, from, to)
        verify(mNetworkManager, times(1)).loadCompanyNews(symbol, from, to)
    }

    @Test
    fun loadCompanyQuote() {
        val symbol = "symbol"
        val position = 1
        mRemoteManager.loadCompanyQuote(symbol, position)
        verify(mNetworkManager, times(1)).loadCompanyQuote(symbol, position)
    }
}