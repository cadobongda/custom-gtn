/*
 * Copyright (C) 2011 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.exoplatform.web.controller.router;

import junit.framework.TestCase;
import org.exoplatform.web.controller.regexp.RENode;
import org.exoplatform.web.controller.regexp.REParser;

import java.util.List;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 */
public class TestFooRenderer extends TestCase
{
   
   private static void assertSatisfied(String expression, String... expected) throws Exception
   {
      RENode root = new REParser(expression).parse();
      List<ValueResolverFactory.Alternative> alternatives = new ValueResolverFactory().foo(root);
      assertEquals(expected.length, alternatives.size());
      for (int i = 0;i < expected.length;i++)
      {
         assertEquals(expected[i], alternatives.get(i).getResolvingExpression().toString());
      }
   }

   public void testDisjunction() throws Exception
   {
      assertSatisfied("a|b", "a", "b");
   }

   public void testAlternative() throws Exception
   {
      assertSatisfied("ab", "ab");
   }

   public void testCharacterClassRange() throws Exception
   {
      assertSatisfied("[b-c]", "b");
   }
   public void testCharacterClassOr() throws Exception
   {
      assertSatisfied("[bc]", "b");
   }

   public void testCharacterClassAnd() throws Exception
   {
      assertSatisfied("[b&&b]", "b");
   }

   public void testQuantifier() throws Exception
   {
      assertSatisfied("b{2,3}", "bb");
   }

   public void testCharacterClassNegateChar() throws Exception
   {
      assertSatisfied("[^a]", " ");
   }

   public void testCharacterClassNegateOr() throws Exception
   {
      assertSatisfied("[^ !]", "\"");
   }

   public void testCharacterClassNegateAnd() throws Exception
   {
      assertSatisfied("[^a&&b]", " ");
   }

   public void testAny() throws Exception
   {
      assertSatisfied(".", "a");
   }

   public void testNonCapturingGroup() throws Exception
   {
      assertSatisfied("(?:a|b)", "a");
   }

   public void testGroup() throws Exception
   {
      assertSatisfied("(a|b)", "(a|b)");
   }

   public void testComplex() throws Exception
   {
      assertSatisfied("[a-z&&f-t&&p-q]", "p");
   }
}
