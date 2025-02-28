/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: MIT-0
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this
 * software and associated documentation files (the "Software"), to deal in the Software
 * without restriction, including without limitation the rights to use, copy, modify,
 * merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.amazon.sample.ui.services.metadata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Data;

@Data
public class Metadata {

  private List<MetadataSet> sets;

  public Metadata() {
    this.sets = new ArrayList<>();
  }

  public Metadata add(MetadataSet set) {
    this.sets.add(set);

    return this;
  }

  public List<MetadataSet> getSets() {
    return this.sets;
  }

  public List<MetadataAttribute> getQualifiedAttributes() {
    return this.sets.stream()
      .flatMap(s ->
        s
          .getAttributes()
          .stream()
          .map(a ->
            new MetadataAttribute(
              s.getName() + '.' + a.getName(),
              a.getValue(),
              a.getProvider()
            )
          )
      )
      .collect(Collectors.toList());
  }

  public MetadataAttribute getAttribute(String qualifiedName) {
    var parts = qualifiedName.split("\\.");

    if (parts.length < 2) {
      return null;
    }

    var setName = parts[0];
    var attributeName = String.join(
      ".",
      Arrays.copyOfRange(parts, 1, parts.length)
    );

    var set =
      this.sets.stream().filter(s -> s.getName().equals(setName)).findFirst();

    if (!set.isPresent()) {
      return null;
    }

    return set.get().getAttribute(attributeName);
  }
}
