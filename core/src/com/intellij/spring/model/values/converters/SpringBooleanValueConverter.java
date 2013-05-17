package com.intellij.spring.model.values.converters;

import com.intellij.util.xml.converters.values.BooleanValueConverter;
import com.intellij.util.ArrayUtil;
import org.jetbrains.annotations.NonNls;

import java.util.Arrays;

/**
 * User: Sergey.Vasiliev
 */
public class SpringBooleanValueConverter extends BooleanValueConverter {
  @NonNls private static final String[] VALUES_TRUE = {"true", "on", "yes", "1"};
  @NonNls private static final String[] VALUES_FALSE = {"false", "off", "no", "0"};

  private static final String[] SORTED_VALUES = ArrayUtil.mergeArrays(VALUES_TRUE, VALUES_FALSE, String.class);

  public SpringBooleanValueConverter(final boolean allowEmpty) {
    super(allowEmpty);
  }

  static {
    Arrays.sort(SORTED_VALUES);
    Arrays.sort(VALUES_TRUE);
  }

  public String[] getTrueValues() {
    return VALUES_TRUE;
  }

  public String[] getFalseValues() {
    return VALUES_FALSE;
  }
}
