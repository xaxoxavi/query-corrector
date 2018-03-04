package com.esliceu.extractor;

import java.util.List;

/**
 * Created by xavi on 4/03/18.
 */
public interface QueryExtractor {
    List<Query> extractSolutionQueries();

    List<QueryWrapper> extractQueries();
}
