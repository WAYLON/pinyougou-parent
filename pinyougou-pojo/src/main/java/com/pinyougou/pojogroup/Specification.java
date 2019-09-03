package com.pinyougou.pojogroup;

import com.pinyougou.pojo.TbSpecification;
import com.pinyougou.pojo.TbSpecificationOption;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
/**规格组合实体类
 * @author wangl
 */
@Data
public class Specification implements Serializable {

    private TbSpecification specification;

    private List<TbSpecificationOption> specificationOptionList;
}
