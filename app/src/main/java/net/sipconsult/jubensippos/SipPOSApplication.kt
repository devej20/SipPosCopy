package net.sipconsult.jubensippos

import android.app.Application
import android.widget.Toast
import com.jakewharton.threetenabp.AndroidThreeTen
import net.sipconsult.jubensippos.data.datasource.discountTypes.local.DiscountTypesLocalDataSource
import net.sipconsult.jubensippos.data.datasource.discountTypes.local.DiscountTypesLocalDataSourceImpl
import net.sipconsult.jubensippos.data.datasource.discountTypes.network.DiscountTypesNetworkDataSource
import net.sipconsult.jubensippos.data.datasource.discountTypes.network.DiscountTypesNetworkDataSourceImpl
import net.sipconsult.jubensippos.data.datasource.location.local.LocationLocalDataSource
import net.sipconsult.jubensippos.data.datasource.location.local.LocationLocalDataSourceImpl
import net.sipconsult.jubensippos.data.datasource.location.network.LocationNetworkDataSource
import net.sipconsult.jubensippos.data.datasource.location.network.LocationNetworkDataSourceImpl
import net.sipconsult.jubensippos.data.datasource.paymentMethod.local.PaymentMethodLocalDataSource
import net.sipconsult.jubensippos.data.datasource.paymentMethod.local.PaymentMethodLocalDataSourceImpl
import net.sipconsult.jubensippos.data.datasource.paymentMethod.network.PaymentMethodNetworkDataSource
import net.sipconsult.jubensippos.data.datasource.paymentMethod.network.PaymentMethodNetworkDataSourceImpl
import net.sipconsult.jubensippos.data.datasource.product.local.ProductLocalDataSource
import net.sipconsult.jubensippos.data.datasource.product.local.ProductLocalDataSourceImpl
import net.sipconsult.jubensippos.data.datasource.product.network.ProductNetworkDataSource
import net.sipconsult.jubensippos.data.datasource.product.network.ProductNetworkDataSourceImpl
import net.sipconsult.jubensippos.data.datasource.salesAgent.local.SalesAgentLocalDataSource
import net.sipconsult.jubensippos.data.datasource.salesAgent.local.SalesAgentLocalDataSourceImpl
import net.sipconsult.jubensippos.data.datasource.salesAgent.network.SalesAgentNetworkDataSource
import net.sipconsult.jubensippos.data.datasource.salesAgent.network.SalesAgentNetworkDataSourceImpl
import net.sipconsult.jubensippos.data.datasource.transaction.local.TransactionLocalDataSource
import net.sipconsult.jubensippos.data.datasource.transaction.local.TransactionLocalDataSourceImpl
import net.sipconsult.jubensippos.data.datasource.transaction.network.TransactionNetworkDataSource
import net.sipconsult.jubensippos.data.datasource.transaction.network.TransactionNetworkDataSourceImpl
import net.sipconsult.jubensippos.data.datasource.user.UserDataSource
import net.sipconsult.jubensippos.data.datasource.user.UserDataSourceImpl
import net.sipconsult.jubensippos.data.db.ApplicationDatabase
import net.sipconsult.jubensippos.data.network.ConnectivityInterceptor
import net.sipconsult.jubensippos.data.network.ConnectivityInterceptorImpl
import net.sipconsult.jubensippos.data.network.SipShopApiService
import net.sipconsult.jubensippos.data.provider.LocationProvider
import net.sipconsult.jubensippos.data.provider.LocationProviderImpl
import net.sipconsult.jubensippos.data.provider.PosNumberProvider
import net.sipconsult.jubensippos.data.provider.PosNumberProviderImpl
import net.sipconsult.jubensippos.data.repository.discountType.DiscountTypeRepository
import net.sipconsult.jubensippos.data.repository.discountType.DiscountTypeRepositoryImpl
import net.sipconsult.jubensippos.data.repository.location.LocationRepository
import net.sipconsult.jubensippos.data.repository.location.LocationRepositoryImpl
import net.sipconsult.jubensippos.data.repository.paymentMethod.PaymentMethodRepository
import net.sipconsult.jubensippos.data.repository.paymentMethod.PaymentMethodRepositoryImpl
import net.sipconsult.jubensippos.data.repository.product.ProductRepository
import net.sipconsult.jubensippos.data.repository.product.ProductRepositoryImpl
import net.sipconsult.jubensippos.data.repository.salesAgent.SalesAgentRepository
import net.sipconsult.jubensippos.data.repository.salesAgent.SalesAgentRepositoryImpl
import net.sipconsult.jubensippos.data.repository.transaction.TransactionRepository
import net.sipconsult.jubensippos.data.repository.transaction.TransactionRepositoryImpl
import net.sipconsult.jubensippos.data.repository.user.UserRepository
import net.sipconsult.jubensippos.data.repository.user.UserRepositoryImpl
import net.sipconsult.jubensippos.ui.category.CategoryViewModelFactory
import net.sipconsult.jubensippos.ui.home.HomeViewModelFactory
import net.sipconsult.jubensippos.ui.login.LoginViewModelFactory
import net.sipconsult.jubensippos.ui.payment.PaymentViewModelFactory
import net.sipconsult.jubensippos.ui.payment.paymentmethod.loyalty.LoyaltyViewModelFactory
import net.sipconsult.jubensippos.ui.products.ProductViewModelFactory
import net.sipconsult.jubensippos.ui.receipt.ReceiptViewModelFactory
import net.sipconsult.jubensippos.ui.settings.SettingsViewModelFactory
import net.sipconsult.jubensippos.ui.transactions.SalesTransactionViewModelFactory
import net.sipconsult.jubensippos.ui.transactions.refund.RefundViewModelFactory
import net.sipconsult.jubensippos.util.FileUtils
import net.sipconsult.jubensippos.util.SunmiPrintHelper
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton
import sunmi.sunmiui.utils.LogUtil
import java.io.FileNotFoundException
import java.io.PrintStream

class SipPOSApplication : Application(), KodeinAware {
    override val kodein = Kodein.lazy {
        import(androidXModule(this@SipPOSApplication))

        bind() from singleton { ApplicationDatabase(instance()) }
        bind() from singleton { instance<ApplicationDatabase>().userDao() }
        bind() from singleton { instance<ApplicationDatabase>().productsDao() }
        bind() from singleton { instance<ApplicationDatabase>().clientsDao() }
        bind() from singleton { instance<ApplicationDatabase>().discountTypeDao() }
        bind() from singleton { instance<ApplicationDatabase>().paymentMethodDao() }
        bind() from singleton { instance<ApplicationDatabase>().locationDao() }
        bind() from singleton { instance<ApplicationDatabase>().salesAgentDao() }

        bind<ConnectivityInterceptor>() with singleton { ConnectivityInterceptorImpl(instance()) }
        bind() from singleton { SipShopApiService(instance()) }
        bind<ProductNetworkDataSource>() with singleton {
            ProductNetworkDataSourceImpl(
                instance()
            )
        }

        bind<PaymentMethodNetworkDataSource>() with singleton {
            PaymentMethodNetworkDataSourceImpl(
                instance()
            )
        }
        bind<PaymentMethodLocalDataSource>() with singleton {
            PaymentMethodLocalDataSourceImpl(
                instance()
            )
        }
        bind<ProductLocalDataSource>() with singleton {
            ProductLocalDataSourceImpl(
                instance()
            )
        }

        bind<TransactionLocalDataSource>() with singleton {
            TransactionLocalDataSourceImpl()
        }
        bind<TransactionNetworkDataSource>() with singleton {
            TransactionNetworkDataSourceImpl(
                instance()
            )
        }

        bind<DiscountTypesLocalDataSource>() with singleton {
            DiscountTypesLocalDataSourceImpl(
                instance()
            )
        }
        bind<DiscountTypesNetworkDataSource>() with singleton {
            DiscountTypesNetworkDataSourceImpl(
                instance()
            )
        }

        bind<LocationLocalDataSource>() with singleton {
            LocationLocalDataSourceImpl(
                instance()
            )
        }
        bind<LocationNetworkDataSource>() with singleton {
            LocationNetworkDataSourceImpl(
                instance()
            )
        }

        bind<SalesAgentLocalDataSource>() with singleton {
            SalesAgentLocalDataSourceImpl(
                instance()
            )
        }
        bind<SalesAgentNetworkDataSource>() with singleton {
            SalesAgentNetworkDataSourceImpl(
                instance()
            )
        }

        bind<UserDataSource>() with singleton {
            UserDataSourceImpl(
                instance(),
                instance()
            )
        }



        bind<ProductRepository>() with singleton {
            ProductRepositoryImpl(
                instance(),
                instance()
            )
        }
        bind<TransactionRepository>() with singleton {
            TransactionRepositoryImpl(
                instance(),
                instance()
            )
        }

        bind<DiscountTypeRepository>() with singleton {
            DiscountTypeRepositoryImpl(
                instance(),
                instance()
            )
        }

        bind<PaymentMethodRepository>() with singleton {
            PaymentMethodRepositoryImpl(
                instance(),
                instance()
            )
        }

        bind<LocationRepository>() with singleton {
            LocationRepositoryImpl(
                instance(),
                instance()
            )
        }

        bind<SalesAgentRepository>() with singleton {
            SalesAgentRepositoryImpl(
                instance(),
                instance()
            )
        }

        bind<UserRepository>() with singleton {
            UserRepositoryImpl(
                instance(),
                this@SipPOSApplication
            )
        }

        bind<LocationProvider>() with singleton {
            LocationProviderImpl(this@SipPOSApplication)
        }
        bind<PosNumberProvider>() with singleton {
            PosNumberProviderImpl(this@SipPOSApplication)
        }

        bind() from provider { HomeViewModelFactory(instance()) }
        bind() from provider { CategoryViewModelFactory(instance()) }
        bind() from provider { ProductViewModelFactory(instance(), instance()) }
        bind() from provider { LoginViewModelFactory(instance()) }

        bind() from provider {
            SharedViewModelFactory(
                instance(),
                instance(),
                instance(),
                instance(),
                instance(),
                instance(),
                instance(),
                this@SipPOSApplication
            )
        }
        bind() from provider { ReceiptViewModelFactory(instance()) }

        bind() from provider { PaymentViewModelFactory(instance()) }

        bind() from provider { SettingsViewModelFactory(instance()) }

        bind() from provider { LoyaltyViewModelFactory(instance()) }

        bind() from provider { SalesTransactionViewModelFactory(instance(), instance()) }

        bind() from provider { RefundViewModelFactory(instance()) }
    }

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
        init()
    }


    /**
     * Connect print service through interface library
     */
    fun init() {
        SunmiPrintHelper.instance.initSunmiPrinterService(this)
//        configUncaughtExceptionHandler()
    }

    /**
     * 捕获异常
     */
    private fun configUncaughtExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler { thread, ex ->
            LogUtil.e(TAG, "uncaughtException crash")
            Toast.makeText(this@SipPOSApplication, "crash", Toast.LENGTH_LONG).show()
            try {
                ex.printStackTrace(PrintStream(FileUtils.createErrorFile()))
            } catch (e: FileNotFoundException) {
                LogUtil.e(TAG, "创建异常文件失败")
                e.printStackTrace()
            }
        }
    }

    companion object {
        private const val TAG = "SipPOSApplication"
    }
}