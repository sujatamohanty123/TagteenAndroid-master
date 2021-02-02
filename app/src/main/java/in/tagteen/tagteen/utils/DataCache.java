package in.tagteen.tagteen.utils;

import java.util.ArrayList;
import java.util.List;

import in.tagteen.tagteen.Model.knowledge.KnowledgeCategories;

public class DataCache {
    private static List<KnowledgeCategories.Category> knowledgeCategories = new ArrayList<>();

    public static void addKnowledgeCategories(List<KnowledgeCategories.Category> categories) {
        knowledgeCategories.clear();
        if (!categories.isEmpty()) {
            knowledgeCategories.addAll(categories);
        }
    }

    public static List<KnowledgeCategories.Category> getKnowledgeCategories() {
        return knowledgeCategories;
    }
}
