package quartztop.analitics.handlers;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import quartztop.analitics.responses.stock.stockResponse.StockByCategoryResponse;
import quartztop.analitics.responses.stock.stockResponse.StockByProductResponse;
import quartztop.analitics.responses.stock.stockResponse.StockByStoreResponse;
import quartztop.analitics.models.products.ProductAttributeEntity;
import quartztop.analitics.models.products.ProductsEntity;
import quartztop.analitics.models.reports.StockByStoreEntity;
import quartztop.analitics.services.reports.ReportStockByStoreService;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class StockByProductAndStoreHandler {

    private final ReportStockByStoreService reportStockByStoreService;

    public List<StockByCategoryResponse> getResponseStockByProductAndStore(String requestStock) {

        List<StockByProductResponse> listStockByProductResponse = new ArrayList<>();

        // Тут ищется список остатков товаров которые попадают в поисковый запрос
        List<StockByStoreEntity> stockByStoreEntityList = reportStockByStoreService.getStockBySearch(requestStock);

        Map<ProductsEntity, List<StockByStoreResponse>> mapProductResponse = new HashMap<>();

        for (StockByStoreEntity stockByStoreEntity : stockByStoreEntityList) {

            if (stockByStoreEntity.getStock() == 0 && stockByStoreEntity.getInTransit() == 0) continue;


            ProductsEntity productsEntity = stockByStoreEntity.getProductsEntity();
            if (productsEntity.getCategoryEntity() == null) continue;

            StockByStoreResponse stockByStoreResponse = new StockByStoreResponse();

            if (stockByStoreEntity.getStoreEntity().getNameToBot() != null) {
                stockByStoreResponse.setNameStore(stockByStoreEntity.getStoreEntity().getNameToBot());

            } else {
                stockByStoreResponse.setNameStore(stockByStoreEntity.getStoreEntity().getName());
            }
            stockByStoreResponse.setStock(stockByStoreEntity.getStock());
            stockByStoreResponse.setInTransit(stockByStoreEntity.getInTransit());
            stockByStoreResponse.setReserve(stockByStoreEntity.getReserve());
            // Собираем по продукту
            mapProductResponse.computeIfAbsent(productsEntity, k -> new ArrayList<>()).add(stockByStoreResponse);
        }

        // Преобразуем map в список по товарам
        for (Map.Entry<ProductsEntity, List<StockByStoreResponse>> entry : mapProductResponse.entrySet()) {
            ProductsEntity product = entry.getKey();
            List<StockByStoreResponse> stores = entry.getValue();
            StockByProductResponse responseByProduct = new StockByProductResponse();
            responseByProduct.setOrderInBotIndex(product.getCategoryEntity().getOrderInBotIndex());

            responseByProduct.setArticle(product.getArticle());
            responseByProduct.setProductName(product.getName());
            responseByProduct.setArticle(product.getArticle());
            responseByProduct.setByStoreResponseList(stores);

            responseByProduct.setCategory(product.getCategoryEntity().getName());

            List<ProductAttributeEntity> productAttributeEntityList = product.getAttributes();
            for(ProductAttributeEntity attribute : productAttributeEntityList) {
                switch (attribute.getName()) {
                    case "Размеры" -> {
                        responseByProduct.setSizeProduct(attribute.getDisplayValue());
                    }
                    case "Формат слэба" -> {
                        responseByProduct.setFormatProduct(attribute.getDisplayValue());
                    }
                    case "Толщина слэба" -> {
                        responseByProduct.setThicknessProduct(attribute.getDisplayValue());
                    }
                    case "Сорт" -> {
                        responseByProduct.setSortProduct(attribute.getDisplayValue());
                    }
                    case "Поверхность" -> {
                        responseByProduct.setSurfaceProduct(attribute.getDisplayValue());
                    }
                    case "РРЦ" -> {
                        responseByProduct.setRecommendedPrice(attribute.getDisplayValue());
                    }
                }
            }
            listStockByProductResponse.add(responseByProduct);
        }

        // Преобразуем список по товарам в список по складам
        Map<String, List<StockByProductResponse>> mapCategoryResponse = new HashMap<>();

        List<StockByCategoryResponse> stockByCategoryResponseList = new ArrayList<>();

        for (StockByProductResponse stockByProductResponse :listStockByProductResponse) {
            String categoryName = stockByProductResponse.getCategory();

            if(categoryName.equals("CALISCO (Турция)") || categoryName.equals("Casablanca (Въетнам)") ||
                    categoryName.equals("GUIDONI (Испания)") || categoryName.equals("Strong Quartz (Китай)") || categoryName.equals("SHANGHAI CSC NEW MATERIAL"))
                categoryName = "Stratos";

            mapCategoryResponse.computeIfAbsent(categoryName, k -> new ArrayList<>()).add(stockByProductResponse);
        }

        for (Map.Entry<String, List<StockByProductResponse>> entry: mapCategoryResponse.entrySet()) {
            String categoryName = entry.getKey();

            StockByCategoryResponse stockByCategoryResponse = new StockByCategoryResponse();
            stockByCategoryResponse.setCategory(categoryName);
            stockByCategoryResponse.setProductsList(entry.getValue());

            stockByCategoryResponseList.add(stockByCategoryResponse);

        }

        // Сортировка по Store orderInBotIndex


        // Сортировка по Category orderInBotIndex
        stockByCategoryResponseList.sort(Comparator.comparingInt(category -> {
            List<StockByProductResponse> products = category.getProductsList();
            return (products != null && !products.isEmpty())
                    ? products.get(0).getOrderInBotIndex()
                    : Integer.MAX_VALUE;
        }));

        // сортировка по article
        for (List<StockByProductResponse> productList : mapCategoryResponse.values()) {
            productList.sort(Comparator.comparing(StockByProductResponse::getArticle));
        }

        return stockByCategoryResponseList;

    }

}
