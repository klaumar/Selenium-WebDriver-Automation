package com.lua.webbuyer.actions;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import com.lua.webbuyer.page.CartPage;
import com.lua.webbuyer.page.HomePage;
import com.lua.webbuyer.page.LoginPage;
import com.lua.webbuyer.page.ProductsPage;
import com.lua.webbuyer.page.ShippingPage;
import com.lua.webbuyer.param.BuyerParams;
import com.lua.webbuyer.param.LoginParams;
import com.lua.webbuyer.resources.ReportExtent;
import com.lua.webbuyer.utils.Common;

public class BuyerActions {

	private WebDriver driver;
	private Common common;
	private HomePage homePage;
	private LoginPage loginPage;
	private ProductsPage productsPage;	
	private CartPage cartPage; 
	private ShippingPage shipping;
	private ReportExtent LOG;
	
	
	
	public BuyerActions(WebDriver driver) {
		this.driver = driver;
		this.common = new Common(driver); 
		this.homePage = new HomePage(driver);
		this.loginPage = new LoginPage(driver); 
		this.productsPage = new ProductsPage(driver);
		this.cartPage = new CartPage(driver);
		this.shipping = new ShippingPage(driver); 
		LOG = new ReportExtent();
	}

	public void productBuy(String Product, String uf) { 


		String user = LoginParams.getUser();  
		String password = LoginParams.getPassword();
		String URL = LoginParams.getURL();
		String cep = BuyerParams.getCep();
		String numero = BuyerParams.getNumeroEndereço();
		String telefone = BuyerParams.getTelefone();
		boolean ufTest = BuyerParams.getUfTest();
		
		try {
			LOG.loggerInfo("Iniciando fluxo de compra para a UF:");
			LOG.loggerInfo("Executando Login com o usuário: '" + user + "', na loja '" + URL + "'");
			loginPage.performLogin(URL, user, password);
			LOG.loggerInfo("Login executado com sucesso!");
			cartPage.cleanCart();
			LOG.loggerInfo("Pesquisando o produto: " + Product);
			homePage.searchItem(Product);
			homePage.productSelect(Product);
			LOG.loggerInfo("Produto adicionado ao carrinho");
			// products.productColorSelectByValue(colorCode);
			productsPage.comprarBtn();
			// productsPage.selectQuantity("3");
			LOG.loggerInfo("Preenchendo dados de shipment");
			cartPage.finalizarCompraBtn();
			shipping.setCep(cep);
			common.loadingWait(driver);
			shipping.setNumero(numero);
			
			if (ufTest == true) {
				shipping.setEstadoUf(uf);
				LOG.loggerInfo("UF selecionada: '" + uf + "'");
			}
			
			shipping.setTelefone(telefone);
			shipping.continuarBtn();
			common.loadingWait(driver);
			shipping.continuarBtn();
			common.loadingWait(driver);
			shipping.efetuarPagamentoBtn();
			common.loadingWait(driver);
			shipping.payTypeBoleto();
			common.loadingWait(driver);
			
			String SuccessMessage = "Pronto, boleto gerado!";
			Assert.assertEquals(shipping.statusCompra(), SuccessMessage);
		} catch (Exception e) {
			Assert.fail(e.getMessage());

		}
	}
}
