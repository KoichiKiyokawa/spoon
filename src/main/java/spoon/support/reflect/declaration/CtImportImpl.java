/**
 * Copyright (C) 2006-2019 INRIA and contributors
 *
 * Spoon is available either under the terms of the MIT License (see LICENSE-MIT.txt) of the Cecill-C License (see LICENSE-CECILL-C.txt). You as the user are entitled to choose the terms under which to adopt Spoon.
 */
package spoon.support.reflect.declaration;

import spoon.SpoonException;
import spoon.experimental.CtUnresolvedImport;
import spoon.reflect.annotations.MetamodelPropertyField;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.declaration.CtImport;
import spoon.reflect.reference.CtPackageReference;
import spoon.reflect.reference.CtReference;
import spoon.reflect.declaration.CtImportKind;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.reference.CtTypeMemberWildcardImportReference;
import spoon.reflect.visitor.CtImportVisitor;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.reflect.reference.CtTypeMemberWildcardImportReferenceImpl;

public class CtImportImpl extends CtElementImpl implements CtImport {
	@MetamodelPropertyField(role = CtRole.IMPORT_REFERENCE)
	private CtReference localReference;

	public CtImportImpl() {
	}

	@Override
	public CtImportKind getImportKind() {
		if (this.localReference == null) {
			return null;
		}

		if (this.localReference instanceof CtFieldReference) {
			return CtImportKind.FIELD;
		} else if (this.localReference instanceof CtExecutableReference) {
			return CtImportKind.METHOD;
		} else if (this.localReference instanceof CtPackageReference) {
			return CtImportKind.ALL_TYPES;
		} else if (this.localReference instanceof CtTypeMemberWildcardImportReferenceImpl) {
			return CtImportKind.ALL_STATIC_MEMBERS;
		} else if (this.localReference instanceof CtTypeReference) {
			return CtImportKind.TYPE;
		} else {
			throw new SpoonException("Only CtFieldReference, CtExecutableReference, CtPackageReference and CtTypeReference are accepted reference types.");
		}
	}

	@Override
	public <T extends CtImport> T setReference(CtReference reference) {
		if (reference != null) {
			reference.setParent(this);
		}
		getFactory().getEnvironment().getModelChangeListener().onObjectUpdate(this, CtRole.IMPORT_REFERENCE, reference, this.localReference);
		this.localReference = reference;
		return (T) this;
	}

	@Override
	public CtReference getReference() {
		return this.localReference;
	}

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtImport(this);
	}

	@Override
	public void accept(CtImportVisitor visitor) {
		switch (getImportKind()) {
		case TYPE:
			visitor.visitTypeImport((CtTypeReference<?>) localReference);
			break;

		case METHOD:
			visitor.visitMethodImport((CtExecutableReference<?>) localReference);
			break;

		case FIELD:
			visitor.visitFieldImport((CtFieldReference<?>) localReference);
			break;

		case ALL_TYPES:
			visitor.visitAllTypesImport((CtPackageReference) localReference);
			break;

		case ALL_STATIC_MEMBERS:
			visitor.visitAllStaticMembersImport((CtTypeMemberWildcardImportReference) localReference);
			break;
		case UNRESOLVED:
			visitor.visitUnresolvedImport((CtUnresolvedImport) localReference);
			break;
		default:
			throw new SpoonException("Unexpected import kind: " + getImportKind());
		}
	}

	@Override
	public CtImportImpl clone() {
		return (CtImportImpl) super.clone();
	}
}
