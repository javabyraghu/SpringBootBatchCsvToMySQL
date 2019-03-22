package com.app.raghu.processor;

import org.springframework.batch.item.ItemProcessor;

import com.app.raghu.model.Product;

public class ProductProcessor implements ItemProcessor<Product, Product> {

	@Override
	public Product process(Product item) throws Exception {
		item.setGst(item.getProdCost()*12/100.0);
		item.setDisc(item.getProdCost()*20/100.0);
		return item;
	}

}
